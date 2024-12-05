package saraf.kartik.moodlog.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_history")
data class MoodHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val mood: String,
    val note: String,
    val timestamp: Long
)