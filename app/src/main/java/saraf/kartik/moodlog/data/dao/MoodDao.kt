package saraf.kartik.moodlog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import saraf.kartik.moodlog.data.model.MoodHistory

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(mood: MoodHistory)

    @Query("SELECT * FROM mood_history WHERE userName = :userName ORDER BY timestamp DESC")
    suspend fun getMoodsForUser(userName: String): List<MoodHistory>

    @Query("DELETE FROM mood_history WHERE userName = :userName")
    suspend fun clearMoodsForUser(userName: String)


}