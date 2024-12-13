package saraf.kartik.moodlog.data.model

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

){
    fun getEmoji(mood: String): String {
        return when (mood) {
            "Happy" -> "😊"
            "Sad" -> "😔"
            "Angry" -> "😡"
            "Anxious" -> "😰"
            "Neutral" -> "😐"
            "Excited" -> "😃"
            else -> "😶" // Default case for unrecognized moods
        }
    }

    companion object{
        val moodStrings = listOf("Happy", "Sad", "Angry", "Anxious", "Neutral", "Excited")
    }

}