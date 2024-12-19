package saraf.kartik.moodlog.view.navigation

sealed class Routes(val route:String) {
    object DailyMoodLoggingRoute : Routes(DAILY_MOOD_LOGGING_VIEW)
    object MoodHistoryRoute : Routes(MOOD_HISTORY_VIEW)
    object MoodAnalysisRoute:Routes(MOOD_ANALYSIS_VIEW)
    object MoodPredictionRoute:Routes(MOOD_PREDICTION_VIEW)

    companion object{
        const val DAILY_MOOD_LOGGING_VIEW = "daily_mood_logging"
        const val MOOD_HISTORY_VIEW = "mood_history"
        const val MOOD_ANALYSIS_VIEW = "mood_analysis"
        const val MOOD_PREDICTION_VIEW = "mood_prediction"

    }
}

