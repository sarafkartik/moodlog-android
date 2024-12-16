package saraf.kartik.moodlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import saraf.kartik.moodlog.data.model.MoodHistory


@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodHistory)

    @Query("UPDATE mood_history SET mood = :mood, note = :note WHERE user_name = :userName AND time_stamp = :timestamp")
    suspend fun updateMood(mood: String, note: String, timestamp: String, userName: String)


    @Query("SELECT * FROM mood_history WHERE user_name = :userName ORDER BY time_stamp DESC")
    suspend fun getMoodsForUser(userName: String): List<MoodHistory>

    @Query("SELECT * FROM mood_history WHERE user_name = :username AND time_stamp = :logDate LIMIT 1")
    suspend fun getMoodEntryForDay(username: String, logDate: String): MoodHistory?

    @Query("DELETE FROM mood_history WHERE user_name = :userName")
    suspend fun clearMoodsForUser(userName: String)


}