package saraf.kartik.moodlog.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.data.model.MoodUtils

@Composable
fun MoodHistoryItem(mood: MoodHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.white))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = MoodUtils.getEmoji(mood.mood),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(text = mood.mood, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = mood.note.ifEmpty { mood.mood },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = mood.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}