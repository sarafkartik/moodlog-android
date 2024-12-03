package saraf.kartik.moodlog.utility

object Constants {
    val moods = listOf(
        Mood("\uD83D\uDE0A", "Happy"),  // 😊
        Mood("\uD83D\uDE14", "Sad"),    // 😔
        Mood("\uD83D\uDE21", "Angry"),  // 😡
        Mood("\uD83D\uDE30", "Anxious"), // 😰
        Mood("\uD83D\uDE10", "Neutral"), // 😐
        Mood("\uD83D\uDE03", "Excited")  // 😃
    )

    val moodStrings = listOf("Happy", "Sad", "Angry", "Anxious", "Neutral", "Excited")
}

data class Mood(val emoji: String, val title: String)
