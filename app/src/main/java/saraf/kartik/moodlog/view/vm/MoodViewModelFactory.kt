package saraf.kartik.moodlog.view.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import saraf.kartik.moodlog.MoodLogApplication

@Suppress("UNCHECKED_CAST")
class MoodViewModelFactory(private val application: MoodLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
            return MoodViewModel(application = application) as T
        }
        return super.create(modelClass)
    }
}