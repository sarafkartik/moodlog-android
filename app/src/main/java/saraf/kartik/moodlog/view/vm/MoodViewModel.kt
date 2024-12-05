package saraf.kartik.moodlog.view.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import saraf.kartik.moodlog.MoodLogApplication
import saraf.kartik.moodlog.app.MoodUseCase
import saraf.kartik.moodlog.data.MoodHistoryDatabase
import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository
import saraf.kartik.moodlog.utility.DateUtil

class MoodViewModel(application: MoodLogApplication) : ViewModel() {
    private val db = MoodHistoryDatabase.build(application.baseContext)
    private val repository = MoodHistoryRepository(db.moodDao())
    private val moodUseCase = MoodUseCase(repository)


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
        repository.let { repo ->
            viewModelScope.launch {
                repo.clearMoodsForUser(userName)
            }
        }
    }

}
