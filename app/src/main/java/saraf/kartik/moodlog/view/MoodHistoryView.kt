package saraf.kartik.moodlog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import saraf.kartik.moodlog.MoodLogApplication
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.data.model.MoodHistory
import saraf.kartik.moodlog.view.vm.MoodViewModel
import saraf.kartik.moodlog.view.vm.MoodViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryView(
    userName: String,
    context: Context,
    onBack: () -> Unit,
) {
    val moodViewModel: MoodViewModel = viewModel(
        factory = MoodViewModelFactory(context.applicationContext as MoodLogApplication)
    )
    val moodHistory by moodViewModel.moodHistory.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userName) {
        moodViewModel.loadMoodHistory(
            userName,
            stopLoading = {
                isLoading = false
            }
        )

    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(R.string.mood_history),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.lavender)
                )
            }, navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.moodlog_icon),
                    contentDescription = "Loading",
                    modifier = Modifier.size(150.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(colorResource(R.color.soft_gray).copy(alpha = 0.3f))
            ) {
                if (moodHistory == null || moodHistory!!.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_moods_logged_yet),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.7F)
                        )
                    }
                } else {
                    val fMoodHistory = moodViewModel.getFilteredMoodHistory(moodHistory)
                    if (!fMoodHistory.isNullOrEmpty()) {
                        MoodComparisonChart(
                            filteredMoodHistory = moodViewModel.getFilteredMoodHistory(
                                moodHistory
                            )!!
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(moodHistory!!.size) { index ->
                            MoodHistoryItem(mood = moodHistory!![index])
                        }
                    }
                }
            }
        }
    }
}

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
                text = mood.getEmoji(mood.mood),
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

@Composable
fun MoodComparisonChart(filteredMoodHistory: List<MoodHistory>) {
    val moodStrings = MoodHistory.moodStrings
    val moodCounts = mutableMapOf<String, Int>().apply {
        moodStrings.forEach { this[it] = 0 }
    }
    filteredMoodHistory.forEach { moodHistoryEntry ->
        moodCounts[moodHistoryEntry.mood] = moodCounts[moodHistoryEntry.mood]?.plus(1) ?: 1
    }
    val percentages = moodCounts.mapValues { (it.value.toDouble() / 7.0) * 100.0 }
    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodStrings.forEach { mood ->
                val percentage = percentages[mood] ?: 0.0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier
                            .height(200.dp)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Text(text = "${percentage.toInt()}%", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height((percentage * 2).dp)
                                .background(colorResource(R.color.pale_pink))
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = mood, fontSize = 12.sp)
                }
            }
        }
        Text(
            text = stringResource(R.string.mood_trends_last_7_days),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MoodHistoryViewPreview() {
    MoodHistoryView(
        userName = "k",
        context = LocalContext.current,
        onBack = {

        }
    )
}

