package saraf.kartik.moodlog.app

import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository

class MoodUseCase(private val moodHistoryRepository: MoodHistoryRepository?) {
    suspend fun insertMood(mood: MoodHistory) {
        if (moodHistoryRepository != null) {
            val moodEntry = moodHistoryRepository.getMoodEntryForDay(
                userName = mood.userName,
                logDate = mood.timestamp
            )
            if (moodEntry != null) {
                moodHistoryRepository.updateMood(mood)
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