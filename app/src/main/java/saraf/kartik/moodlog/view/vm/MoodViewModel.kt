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
import saraf.kartik.moodlog.utility.SentimentAnalyzer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MoodViewModel(application: MoodLogApplication) : ViewModel() {
    private val db = MoodHistoryDatabase.build(application.baseContext)
    private val repository = MoodHistoryRepository(db.moodDao())
    private val moodUseCase = MoodUseCase(repository)
    private val sentimentAnalyzer = SentimentAnalyzer(application.baseContext)
    private val prefix = "You have mostly felt"
    private val suffix = ", showing mood stability.\n"
    private val moodFlexibility = "There’s a good variation in your moods, indicating emotional flexibility.\n"
    private val _moodHistory = MutableStateFlow<List<MoodHistory>?>(emptyList())
    private val _moodInsights = MutableStateFlow<Map<String, Double>>(mutableMapOf())
    val moodHistory: StateFlow<List<MoodHistory>?> = _moodHistory
    val moodInsights: StateFlow<Map<String, Double>> = _moodInsights


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

    fun calculateMoodFrequencies(days: Int, userName: String, stopLoading: () -> Unit) {
        viewModelScope.launch {
            val allMoodLogs =  moodUseCase.getMoodsForUser(userName)
            val moodLogs = getFilteredMoodHistory(allMoodLogs)
            if (!moodLogs.isNullOrEmpty()) {
                val moodCount = moodLogs.groupBy { it.mood }
                    .mapValues { (_, logs) -> logs.size.toDouble() }
                val totalLogs = moodLogs.size.toDouble()
                _moodInsights.value = moodCount.mapValues { (_, count) ->
                    if (totalLogs > 0) (count / totalLogs) * 100 else 0.0
                }
            }
            stopLoading()
        }
    }

    fun generateMoodInsights(moodFrequencies:Map<String,Double>,days: Int, userName: String): List<String> {
        val mostFrequentMood = moodFrequencies.maxWithOrNull(compareBy<Map.Entry<String, Double>> { it.value }
            .thenBy { it.key }) ?: return emptyList()

        val leastFrequentMood = moodFrequencies.minWithOrNull(compareBy<Map.Entry<String, Double>> { it.value }
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
