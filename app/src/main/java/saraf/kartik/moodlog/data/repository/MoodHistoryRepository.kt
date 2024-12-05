package saraf.kartik.moodlog.data.repository

import saraf.kartik.moodlog.data.dao.MoodDao
import saraf.kartik.moodlog.data.model.MoodHistory

class MoodHistoryRepository(private val moodDao: MoodDao) {
    suspend fun insertMood(mood: MoodHistory) = moodDao.insertMood(mood)
    suspend fun getMoodsForUser(userName: String) = moodDao.getMoodsForUser(userName)
    suspend fun clearMoodsForUser(userName: String) = moodDao.clearMoodsForUser(userName)
}