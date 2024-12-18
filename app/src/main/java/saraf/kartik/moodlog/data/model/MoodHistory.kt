package saraf.kartik.moodlog.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_history")
data class MoodHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "mood") val mood: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "time_stamp") val timestamp: String

)

object MoodUtils{
    val moodStrings = listOf("Happy", "Sad", "Angry", "Anxious", "Neutral", "Excited")
    fun getEmoji(mood: String): String {
        return when (mood) {
            "Happy" -> "😊"
            "Sad" -> "😔"
            "Angry" -> "😡"
            "Anxious" -> "😰"
            "Neutral" -> "😐"
            "Excited" -> "😃"
            else -> "😶"
        }
    }
    fun getMoodColor(mood: String): Color {
        return when (mood) {
            "Happy" -> Color(0xFFFFEB3B) // Yellow
            "Sad" -> Color(0xFF2196F3)   // Blue
            "Angry" -> Color(0xFFF44336) // Red
            "Anxious" -> Color(0xFF9C27B0) // Purple
            "Neutral" -> Color(0xFF9E9E9E) // Grey
            "Excited" -> Color(0xFFFF9800) // Orange
            else -> Color.Black
        }
    }
}