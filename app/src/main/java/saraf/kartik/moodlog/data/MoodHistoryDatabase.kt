package saraf.kartik.moodlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import saraf.kartik.moodlog.data.dao.MoodDao
import saraf.kartik.moodlog.data.model.MoodHistory

@Database(entities = [MoodHistory::class], version = 1, exportSchema = false)
abstract class MoodHistoryDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: MoodHistoryDatabase? = null

        fun getInstance(context: Context): MoodHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodHistoryDatabase::class.java,
                    "mood_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}