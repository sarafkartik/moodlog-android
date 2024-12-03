import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import saraf.kartik.moodlog.R

@Composable
fun NameInputView(onSave: (String) -> Unit) {
    var userName by remember { mutableStateOf(TextFieldValue("")) }
    val characterLimit = 10
    val isValidName = userName.text.isNotEmpty() && userName.text.length <= characterLimit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Column {
            Text(
                text = stringResource(R.string.name_input_view_heading),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = userName,
                onValueChange = {
                    if (it.text.length <= characterLimit) userName = it
                },
                placeholder = { Text(stringResource(R.string.name_input_view_text_hint)) }, // Replace with your constant
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${userName.text.length} / $characterLimit",
                style = TextStyle(fontSize = 12.sp),
                color = if (userName.text.length > characterLimit) Color.Red else Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = { onSave(userName.text) },
            enabled = isValidName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValidName) colorResource(R.color.soft_blue) else colorResource(R.color.soft_gray)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Continue", color = Color.White) // Replace with your constant
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NameInputViewPreview() {
    NameInputView(onSave = { name -> println("Saved name: $name") })
}

