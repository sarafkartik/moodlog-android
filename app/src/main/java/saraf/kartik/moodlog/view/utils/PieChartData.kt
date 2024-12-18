package saraf.kartik.moodlog.view.utils

import androidx.compose.ui.graphics.Color

data class PieChartData(val slices: List<Slice>) {
    internal val totalLength: Float
        get() {
            return slices.map { it.value }.sum()
        }

    data class Slice(
        val value: Float,
        val color: Color
    )
}