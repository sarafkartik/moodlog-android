package saraf.kartik.moodlog.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import saraf.kartik.moodlog.R
import saraf.kartik.moodlog.utility.Constants

@Composable
fun DailyMoodLoggingView(
    userName: String,
    context: Context,
    //moodManager: MoodManager,
    onCleanSlate: () -> Unit
) {
    var isDrawerOpen by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf("") }
    var reflectionNote by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val moods = Constants.moods
    val characterLimit = 50

    Box(Modifier.fillMaxSize()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isDrawerOpen = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
                Text(
                    text = "Hello, $userName!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Title
            Text(
                text = stringResource(R.string.mood_log_page_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Mood Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(moods) { mood ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mood.emoji,
                            style = MaterialTheme.typography.displayLarge.copy(
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Proportional,
                                    trim = LineHeightStyle.Trim.None
                                )
                            ),
                            textAlign = TextAlign.Center,
                            softWrap = true,
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .height(IntrinsicSize.Min)
                                .background(
                                    if (selectedMood == mood.title) Color.Yellow else Color.Gray.copy(
                                        alpha = 0.1f
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable { selectedMood = mood.title }
                                .padding(20.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = mood.title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reflection Note Input
            OutlinedTextField(
                value = reflectionNote,
                onValueChange = { reflectionNote = it },
                placeholder = { Text(text = stringResource(R.string.mood_log_page_reflection_note_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(6.dp)),
                singleLine = true,
                isError = reflectionNote.length > characterLimit,
                enabled = selectedMood.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${reflectionNote.length} / $characterLimit",
                style = MaterialTheme.typography.bodySmall,
                color = if (reflectionNote.length > characterLimit) Color.Red else Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            TextButton(
                onClick = {
                    //moodManager.saveMood(userName, selectedMood, reflectionNote)
                    selectedMood = ""
                    reflectionNote = ""
                    scope.launch {
                        Toast.makeText(
                            context,
                            context.getString(R.string.mood_saved_message),
                            Toast.LENGTH_LONG
                        ).show()
                        delay(2000)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selectedMood.isNotEmpty() && reflectionNote.length <= characterLimit) colorResource(
                            R.color.soft_green
                        ) else colorResource(R.color.soft_gray), shape = RoundedCornerShape(20.dp)
                    ),
                enabled = selectedMood.isNotEmpty() && reflectionNote.length <= characterLimit,
            ) {
                Text(
                    text = stringResource(R.string.submit_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Reset Button
            TextButton(
                onClick = {
                    selectedMood = ""
                    reflectionNote = ""
                },
                enabled = selectedMood.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selectedMood.isNotEmpty()) colorResource(
                            R.color.soft_red
                        ) else colorResource(R.color.soft_gray), shape = RoundedCornerShape(20.dp)
                    ),
            ) {
                Text(
                    text = stringResource(R.string.reset_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Drawer
        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isDrawerOpen = false }
            )
            DrawerView(
                userName = userName,
                onCleanSlate = {
                    selectedMood = ""
                    reflectionNote = ""
                    onCleanSlate()
                },
                isDrawerOpen = { isDrawerOpen = false }
            )
        }

    }
}

@Composable
fun DrawerView(
    userName: String,
    onCleanSlate: () -> Unit,
    isDrawerOpen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(colorResource(R.color.lavender))
            .padding(16.dp)
    ) {
        TextButton(onClick = {

        }) {
            Text(
                text = stringResource(R.string.mood_history),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(5.dp))
        TextButton(onClick = {

        }) {
            Text(
                text = stringResource(R.string.mood_analysis),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        TextButton(onClick = {

        }) {
            Text(
                text = stringResource(R.string.mood_prediction),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        TextButton(onClick = {
            onCleanSlate()
            isDrawerOpen()
        }) {
            Text(
                text = stringResource(R.string.clean_slate),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyMoodLoggingViewPreview() {
    DailyMoodLoggingView(
        userName = "Kartik",
        context = LocalContext.current,
        onCleanSlate = {

        }
    )
}

