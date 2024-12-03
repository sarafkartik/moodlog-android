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
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.utility.PreferenceManager
import saraf.kartik.moodlog.view.DailyMoodLoggingView

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
                        .background(Color.White) // Replace with desired background color
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moodlog_icon), // Replace with your image resource
                        contentDescription = "Loading",
                        modifier = Modifier.size(150.dp) // Adjust size as needed
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
                DailyMoodLoggingView(userName = userName ?: "", context) {
                    clearUserName(context)
                    userName = null
                }
            }
        }
    }
}


// Helper function to save the user name
fun saveUserName(context: Context, userName: String) {
    PreferenceManager.saveUserName(context, userName)
}

// Helper function to clear the user name
fun clearUserName(context: Context) {
    PreferenceManager.clearUserName(context)
}


@Preview(showBackground = true)
@Composable
fun AppContentViewPreview() {
    AppContentView(context = LocalContext.current)
}


