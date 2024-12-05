package saraf.kartik.moodlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import saraf.kartik.moodlog.data.model.MoodHistory


@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodHistory)

    @Update
    suspend fun updateMood(mood: MoodHistory)

    @Query("SELECT * FROM mood_history WHERE user_name = :userName ORDER BY time_stamp DESC")
    suspend fun getMoodsForUser(userName: String): List<MoodHistory>

    @Query("SELECT * FROM mood_history WHERE user_name = :username AND time_stamp = :logDate LIMIT 1")
    suspend fun getMoodEntryForDay(username: String, logDate: String): MoodHistory?

    @Query("DELETE FROM mood_history WHERE user_name = :userName")
    suspend fun clearMoodsForUser(userName: String)


}