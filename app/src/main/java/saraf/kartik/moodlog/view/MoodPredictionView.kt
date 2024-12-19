package saraf.kartik.moodlog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import saraf.kartik.moodlog.view.components.MoodHistoryItem
import saraf.kartik.moodlog.view.vm.MoodViewModel
import saraf.kartik.moodlog.view.vm.MoodViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodPredictionView(
    userName: String,
    context: Context,
    navController: NavHostController,
) {
    val moodViewModel: MoodViewModel = viewModel(
        factory = MoodViewModelFactory(context.applicationContext as MoodLogApplication)
    )
    val lastThreeMoodsList by moodViewModel.lastThreeDaysMoodHistory.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userName) {
        moodViewModel.getLastThreeMoods(
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
                    stringResource(R.string.mood_prediction),
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
            if(lastThreeMoodsList != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(colorResource(R.color.white))
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item{
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(R.string.last_three_logged_moods),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(lastThreeMoodsList!!.size) { index ->
                        MoodHistoryItem(mood = lastThreeMoodsList!![index])
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.height(12.dp))
                    }
                    item {
                        val prediction = moodViewModel.getMoodPrediction(lastThreeMoodsList)
                        if(prediction != "Unknown"){
                            Column {
                                Text(
                                    text = stringResource(R.string.mood_prediction_for_tomorrow),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .padding()
                                        .background(colorResource(R.color.muted_peach)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = MoodUtils.getEmoji(prediction),
                                        fontSize = 50.sp,
                                        modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
                                    )
                                    Text(
                                        text = prediction,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(start = 4.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                HorizontalDivider(modifier = Modifier.height(12.dp))
                                Text(stringResource(R.string.mood_prediction_disclaimer),style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(20.dp))
                            }
                        }
                    }
                }
            }else{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.mood_prediction_empty_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun MoodPredictionViewPreview() {
    MoodPredictionView(
        userName = "k",
        context = LocalContext.current,
        navController = rememberNavController()
    )
}