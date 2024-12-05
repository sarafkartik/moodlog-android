package saraf.kartik.moodlog.view.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import saraf.kartik.moodlog.MoodLogApplication

class MoodViewModel : ViewModel() {
    private val repository = MoodLogApplication.getMoodHistoryRepository()

    fun clearMoodHistoryForUser(userName: String) {
        repository?.let { repo ->
            viewModelScope.launch {
                repo.clearMoodsForUser(userName)
            }
        }
    }

}
