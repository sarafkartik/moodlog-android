package saraf.kartik.moodlog

import AppContentView
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat

@Composable
fun MoodLogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.White,
            secondary = Color.Black,
            background = Color.White,
            surface = Color.White,
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black
        ),
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Moodlog)
        super.onCreate(savedInstanceState)
        setContent {
            MoodLogTheme {
                AppContentView(this)
            }
        }
    }

}


@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun MainPreview() {
    MoodLogTheme {
      AppContentView(LocalContext.current)
    }
}

