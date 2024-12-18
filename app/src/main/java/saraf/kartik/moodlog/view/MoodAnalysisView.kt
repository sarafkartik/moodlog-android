package saraf.kartik.moodlog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import saraf.kartik.moodlog.MoodLogApplication
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.data.model.MoodUtils
import saraf.kartik.moodlog.view.utils.PieChart
import saraf.kartik.moodlog.view.utils.PieChartData
import saraf.kartik.moodlog.view.vm.MoodViewModel
import saraf.kartik.moodlog.view.vm.MoodViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAnalysisView(
    userName: String,
    context: Context,
    navController: NavHostController,
) {
    val moodViewModel: MoodViewModel = viewModel(
        factory = MoodViewModelFactory(context.applicationContext as MoodLogApplication)
    )
    val moodInsights by moodViewModel.moodInsights.collectAsState()
    val sentimentAnalysisResult:String by moodViewModel.sentimentAnalysisResult.collectAsState()
    var moodAnalysisInsights: List<String>
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userName) {
        moodViewModel.calculateMoodFrequencies(
            days = 7,
            userName = userName,
            stopLoading = {
                isLoading = false
            }
        )

    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(R.string.mood_analysis),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.lavender)
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
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
            if (moodInsights.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(colorResource(R.color.white))
                ) {
                    item {
                        PieChart(
                            pieChartData = PieChartData(
                                slices = moodInsights.map {
                                    PieChartData.Slice(
                                        it.value.toFloat(),
                                        MoodUtils.getMoodColor(it.key)
                                    )
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(20.dp),
                        )
                    }
                    item {
                        MoodLegend(
                            currentMoods = moodInsights.keys.toList(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    items(
                        count = moodInsights.size,
                        key = { index -> moodInsights.keys.elementAt(index) }
                    ) { index ->
                        val mood = moodInsights.keys.elementAt(index)
                        val percentage = moodInsights[mood]?.let { "%.1f".format(it) } ?: "0.0"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mood,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                    item {
                        Column {
                            Spacer(modifier = Modifier.height(5.dp))
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        }


                    }
                    item {
                        moodAnalysisInsights =
                            moodViewModel.generateMoodInsights(moodInsights,7,userName)
                        Column {
                            Spacer(modifier = Modifier.height(36.dp))
                            Text(
                                text = stringResource(R.string.mood_insights),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .background(Color.LightGray.copy(alpha = 0.7f))
                                    .padding(16.dp)
                            ) {
                                Row {
                                    Text(
                                        text = stringResource(R.string.most_prevalent_mood),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = moodAnalysisInsights.getOrNull(0) ?: "N/A",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Row {
                                    Text(
                                        text = stringResource(R.string.least_prevalent_mood),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = moodAnalysisInsights.getOrNull(1) ?: "N/A",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.result_label),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sentimentAnalysisResult,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    item {
                        Text(stringResource(R.string.sentiment_analysis_disclaimer),style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(20.dp))
                    }

                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.mood_analysis_empty_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

    }
}


@Composable
fun MoodLegend(currentMoods: List<String>, modifier: Modifier = Modifier) {
    val moodColors = mapOf(
        "Happy" to Color(0xFFFFEB3B),
        "Sad" to Color(0xFF2196F3),
        "Angry" to Color(0xFFF44336),
        "Anxious" to Color(0xFF9C27B0),
        "Neutral" to Color(0xFF9E9E9E),
        "Excited" to Color(0xFFFF9800)
    )

    // Filter the moodColors to include only those in currentMoods
    val filteredMoods = moodColors.filterKeys { it in currentMoods }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        filteredMoods.forEach { (mood, color) ->
            MoodLegendItem(mood = mood, color = color)
        }
    }
}

@Composable
fun MoodLegendItem(mood: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = mood,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoodAnalysisViewPreview() {
    MoodAnalysisView(
        userName = "k",
        context = LocalContext.current,
        navController = rememberNavController()
    )
}