package saraf.kartik.moodlog

import android.app.Application
import saraf.kartik.moodlog.data.MoodHistoryDatabase
import saraf.kartik.moodlog.data.repository.MoodHistoryRepository

class MoodLogApplication: Application() {
    private val db by lazy { MoodHistoryDatabase.getInstance(this) }
    val moodHistoryRepository by lazy { MoodHistoryRepository(db.moodDao()) }

    companion object {
        private var instance: MoodLogApplication? = null

        fun getMoodHistoryRepository(): MoodHistoryRepository? {
            return instance?.moodHistoryRepository
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}