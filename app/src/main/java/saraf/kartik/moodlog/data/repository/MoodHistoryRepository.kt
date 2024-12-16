package saraf.kartik.moodlog.data.repository

import saraf.kartik.moodlog.data.dao.MoodDao
import saraf.kartik.moodlog.data.model.MoodHistory

class MoodHistoryRepository(private val moodDao: MoodDao) {
    suspend fun insertMood(mood: MoodHistory) = moodDao.insertMood(mood)
    suspend fun updateMood(mood: String, note:String, timestamp: String, userName: String) = moodDao.updateMood(mood, note, timestamp, userName)
    suspend fun getMoodEntryForDay(userName: String, logDate: String) = moodDao.getMoodEntryForDay(
        username = userName,
        logDate = logDate
    )

    suspend fun getMoodsForUser(userName: String) = moodDao.getMoodsForUser(userName)
    suspend fun clearMoodsForUser(userName: String) = moodDao.clearMoodsForUser(userName)


}