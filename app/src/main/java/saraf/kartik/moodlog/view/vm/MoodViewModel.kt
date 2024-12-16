package saraf.kartik.moodlog.view.vm

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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MoodViewModel(application: MoodLogApplication) : ViewModel() {
    private val db = MoodHistoryDatabase.build(application.baseContext)
    private val repository = MoodHistoryRepository(db.moodDao())
    private val moodUseCase = MoodUseCase(repository)
    private val _moodHistory = MutableStateFlow<List<MoodHistory>?>(emptyList())
    val moodHistory: StateFlow<List<MoodHistory>?> = _moodHistory

    fun loadMoodHistory(userName: String, stopLoading:() -> Unit) {
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

}
