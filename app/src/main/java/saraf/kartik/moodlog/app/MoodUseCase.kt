package saraf.kartik.moodlog.app

import android.util.Log
import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository

class MoodUseCase(private val moodHistoryRepository: MoodHistoryRepository?) {
    suspend fun insertMood(mood: MoodHistory) {
        if (moodHistoryRepository != null) {
            val moodEntry = moodHistoryRepository.getMoodEntryForDay(
                userName = mood.userName,
                logDate = mood.timestamp
            )
            Log.e("MoodEntry","${moodEntry}")
            if (moodEntry != null) {
                Log.e("MoodEntry","Updating")
                moodHistoryRepository.updateMood(mood = mood.mood, note = mood.note, timestamp = mood.timestamp, userName = mood.userName)
            } else {
                moodHistoryRepository.insertMood(mood)
            }
        }
        return
    }

    suspend fun getMoodsForUser(userName: String) = moodHistoryRepository?.getMoodsForUser(userName)
    suspend fun clearMoodsForUser(userName: String) =
        moodHistoryRepository?.clearMoodsForUser(userName)
}