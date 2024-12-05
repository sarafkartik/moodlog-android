package saraf.kartik.moodlog.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import saraf.kartik.moodlog.view.vm.MoodViewModel

@Suppress("UNCHECKED_CAST")
class MoodViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
            return MoodViewModel() as T
        }
        return super.create(modelClass)
    }
}