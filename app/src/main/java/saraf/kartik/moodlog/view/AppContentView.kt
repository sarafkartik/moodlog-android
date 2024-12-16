import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.utility.PreferenceManager
import saraf.kartik.moodlog.view.DailyMoodLoggingView
import saraf.kartik.moodlog.view.MoodHistoryView
import saraf.kartik.moodlog.view.navigation.Routes

@Composable
fun AppContentView(context: Context) {
    var userName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userName = PreferenceManager.getUserName(context)
        isLoading = false
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
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
            }

            userName == null -> {
                NameInputView { name ->
                    saveUserName(context, name)
                    userName = name
                }
            }

            userName != null && userName!!.isNotEmpty() -> {
                NavHostView(userName ?: "", context) {
                    clearUserName(context)
                    userName = null
                }

            }
        }
    }
}

@Composable
fun NavHostView(
    userName: String,
    context: Context,
    onCleanSlate: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.DailyMoodLoggingRoute.route) {
        composable(route = Routes.DAILY_MOOD_LOGGING_VIEW) {
            DailyMoodLoggingView(userName = userName, context, onCleanSlate, navController)
        }
        composable(route = Routes.MOOD_HISTORY_VIEW) {
            MoodHistoryView(userName = userName, context = context, navController = navController)
        }
    }

}


fun saveUserName(context: Context, userName: String) {
    PreferenceManager.saveUserName(context, userName)
}

fun clearUserName(context: Context) {
    PreferenceManager.clearUserName(context)
}


@Preview(showBackground = true)
@Composable
fun AppContentViewPreview() {
    AppContentView(context = LocalContext.current)
}


