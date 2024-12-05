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
        private const val dbName = "mood_history_database"

        fun build(context: Context): MoodHistoryDatabase {
            return Room.databaseBuilder(context, MoodHistoryDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}