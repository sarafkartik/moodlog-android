package saraf.kartik.moodlog.app

import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository

class MoodUseCase(private val moodHistoryRepository: MoodHistoryRepository) {
    suspend fun insertMood(mood: MoodHistory) = moodHistoryRepository.insertMood(mood)
    suspend fun getMoodsForUser(userName: String) = moodHistoryRepository.getMoodsForUser(userName)
    suspend fun clearMoodsForUser(userName: String) =
        moodHistoryRepository.clearMoodsForUser(userName)
}