package saraf.kartik.moodlog.view.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import saraf.kartik.moodlog.MoodLogApplication
import saraf.kartik.moodlog.app.MoodUseCase
import saraf.kartik.moodlog.data.MoodHistoryDatabase
import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository
import saraf.kartik.moodlog.utility.DateUtil
import saraf.kartik.moodlog.utility.MoodPredictor
import saraf.kartik.moodlog.utility.SentimentAnalyzer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MoodViewModel(application: MoodLogApplication) : ViewModel() {
    private val db = MoodHistoryDatabase.build(application.baseContext)
    private val repository = MoodHistoryRepository(db.moodDao())
    private val moodUseCase = MoodUseCase(repository)
    private val sentimentAnalyzer = SentimentAnalyzer(application.baseContext)
    private val moodPredictor = MoodPredictor(application.baseContext)
    private val prefix = "You have mostly felt"
    private val suffix = ", showing mood stability.\n"
    private val moodFlexibility =
        "There’s a good variation in your moods, indicating emotional flexibility.\n"
    private val _moodHistory = MutableStateFlow<List<MoodHistory>?>(emptyList())
    private val _moodInsights = MutableStateFlow<Map<String, Double>>(mutableMapOf())
    private val _sentimentAnalysisResult = MutableStateFlow("Unknown")
    private val _lastThreeDaysMoodHistory = MutableStateFlow<List<MoodHistory>?>(emptyList())
    val moodHistory: StateFlow<List<MoodHistory>?> = _moodHistory
    val moodInsights: StateFlow<Map<String, Double>> = _moodInsights
    val sentimentAnalysisResult: StateFlow<String> = _sentimentAnalysisResult
    val lastThreeDaysMoodHistory: StateFlow<List<MoodHistory>?> = _lastThreeDaysMoodHistory


    fun loadMoodHistory(userName: String, stopLoading: () -> Unit) {
        viewModelScope.launch {
            _moodHistory.value = moodUseCase.getMoodsForUser(userName)
            stopLoading()
        }
    }

    fun getFilteredMoodHistory(moodHistoryList: List<MoodHistory>?): List<MoodHistory>? {
        val sevenDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return moodHistoryList?.filter { moodHistory ->
            val moodDate = try {
                dateFormat.parse(moodHistory.timestamp)
            } catch (e: Exception) {
                null
            }
            moodDate?.after(sevenDaysAgo) == true
        }
    }

    fun getLastThreeMoods(userName: String, stopLoading: () -> Unit) {
        viewModelScope.launch {
            val moodHistoryList = moodUseCase.getMoodsForUser(userName)

            val filteredMoods = moodHistoryList?.take(3)
            if (filteredMoods != null && filteredMoods.size >= 3) {
                _lastThreeDaysMoodHistory.value = filteredMoods
            } else {
                _lastThreeDaysMoodHistory.value = null
            }
            stopLoading()
        }
    }

    fun getMoodPrediction(moodHistoryList: List<MoodHistory>?): String {
        if (moodHistoryList != null && moodHistoryList.size >= 3) {
            return moodPredictor.predictMood(
                mood1 = moodHistoryList[0].mood,
                mood2 = moodHistoryList[1].mood,
                mood3 = moodHistoryList[2].mood
            )
        }
        return "Unknown"
    }

    fun calculateMoodFrequencies(days: Int, userName: String, stopLoading: () -> Unit) {
        viewModelScope.launch {
            val allMoodLogs = moodUseCase.getMoodsForUser(userName)
            val moodLogs = getFilteredMoodHistory(allMoodLogs)
            if (!moodLogs.isNullOrEmpty()) {
                val moodCount = moodLogs.groupBy { it.mood }
                    .mapValues { (_, logs) -> logs.size.toDouble() }
                val totalLogs = moodLogs.size.toDouble()
                _moodInsights.value = moodCount.mapValues { (_, count) ->
                    if (totalLogs > 0) (count / totalLogs) * 100 else 0.0
                }
                _sentimentAnalysisResult.value = calculateMoodSentimentAnalysis(moodLogs)
            }
            stopLoading()
        }
    }

    private fun calculateMoodSentimentAnalysis(moodLogs: List<MoodHistory>): String {
        var result = "Your overall sentiment has been:"
        val sentimentAnalysisResult = mutableListOf<String>()
        if (moodLogs.isNotEmpty()) {
            for (entry in moodLogs) {
                val sentimentAnalysis = sentimentAnalyzer.classifyMood(entry.note)

                sentimentAnalysis.let {
                    if (it.isNotEmpty()) {
                        sentimentAnalysisResult.add(it)
                    }
                }
            }

            if (sentimentAnalysisResult.isNotEmpty()) {
                val sentimentFrequency =
                    calculateSentimentFrequency(sentiments = sentimentAnalysisResult)
                var mostFrequentMood: Map.Entry<String, Double>? = null
                for (entry in sentimentFrequency) {
                    if (mostFrequentMood == null) {
                        mostFrequentMood = entry
                    } else {
                        if ((entry.value > mostFrequentMood.value) || ((entry.value == mostFrequentMood.value) && (entry.key < mostFrequentMood.key))) {
                            mostFrequentMood = entry
                        }
                    }
                }

                Log.i("SentimentAnalyser", " ----- ${mostFrequentMood?.key}")
                if (mostFrequentMood != null) {
                    result += mostFrequentMood.key
                    return result
                }
            }
        }
        result = "No sentiment analysis results available."
        return result
    }

    private fun calculateSentimentFrequency(sentiments: List<String>): Map<String, Double> {
        // Group sentiments by each unique mood and count their occurrences
        val moodCount = sentiments.groupingBy { it }.eachCount()

        // Calculate the total number of sentiments
        val totalLogs = sentiments.size.toDouble()

        // Calculate the frequency (percentage) for each mood
        return moodCount.mapValues { (key, count) ->
            if (totalLogs > 0) {
                (count.toDouble() / totalLogs) * 100
            } else {
                0.0
            }
        }
    }


    fun generateMoodInsights(
        moodFrequencies: Map<String, Double>,
        days: Int,
        userName: String,
    ): List<String> {
        val mostFrequentMood =
            moodFrequencies.maxWithOrNull(compareBy<Map.Entry<String, Double>> { it.value }
                .thenBy { it.key }) ?: return emptyList()

        val leastFrequentMood =
            moodFrequencies.minWithOrNull(compareBy<Map.Entry<String, Double>> { it.value }
                .thenBy { it.key }) ?: return emptyList()

        val stabilityThreshold = 60.0
        val predominantMood = moodFrequencies.entries.firstOrNull { it.value >= stabilityThreshold }
        val result = mutableListOf<String>()
        result.add(mostFrequentMood.key)
        result.add(leastFrequentMood.key)

        if (predominantMood != null) {
            result.add("${prefix}${predominantMood.key}${suffix}")
        } else {
            result.add(moodFlexibility)
        }

        return result
    }


    fun saveMoodEntry(userName: String, mood: String, reflectionNote: String) {
        viewModelScope.launch {
            val note = reflectionNote.ifEmpty { mood }
            moodUseCase.insertMood(
                MoodHistory(
                    userName = userName,
                    mood = mood,
                    note = note,
                    timestamp = DateUtil.getTodayDate()
                )
            )
        }
    }


    fun clearMoodHistoryForUser(userName: String) {
        viewModelScope.launch {
            moodUseCase.clearMoodsForUser(userName)

        }
    }

    override fun onCleared() {
        sentimentAnalyzer.close()
        super.onCleared()
    }


}
