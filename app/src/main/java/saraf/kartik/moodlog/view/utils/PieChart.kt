package saraf.kartik.moodlog.view.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
internal const val FLOAT_10 = 10F
internal const val FLOAT_100 = 100F
internal const val FLOAT_200 = 200F
internal const val FLOAT_255 = 255F
internal const val FLOAT_360 = 360F
internal const val FLOAT_1_5 = 1.5F
internal const val FLOAT_0_5 = .5F
internal fun calculateAngle(
    sliceLength: Float,
    totalLength: Float,
    progress: Float
): Float = FLOAT_360 * sliceLength * progress / totalLength

@Composable
fun PieChart(
    pieChartData: PieChartData,
    modifier: Modifier = Modifier,
    sliceDrawer: ISliceDrawer = SimpleSliceDrawer()
) {
    val transitionProgress = remember(pieChartData.slices) {
        Animatable(initialValue = 0F)
    }

    LaunchedEffect(pieChartData.slices) {
        transitionProgress.animateTo(1F)
    }

    DrawChart(
        pieChartData = pieChartData,
        modifier = modifier.fillMaxSize(),
        progress = transitionProgress.value,
        sliceDrawer = sliceDrawer
    )
}

@Composable
private fun DrawChart(
    pieChartData: PieChartData,
    modifier: Modifier,
    progress: Float,
    sliceDrawer: ISliceDrawer
) {
    val slices = pieChartData.slices

    Canvas(
        modifier = modifier
    ) {
        drawIntoCanvas {
            var startArc = 0F
            slices.forEach { slice ->
                val arc = calculateAngle(
                    sliceLength = slice.value,
                    totalLength = pieChartData.totalLength,
                    progress = progress
                )
                sliceDrawer.drawSlice(
                    drawScope = this,
                    canvas = drawContext.canvas,
                    area = size,
                    startAngle = startArc,
                    sweepAngle = arc,
                    slice = slice
                )
                startArc += arc
            }
        }
    }
}

@Preview
@Composable
private fun PieChartPreview() = PieChart(
    pieChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice(25F, Color.Red),
            PieChartData.Slice(45F, Color.Green),
            PieChartData.Slice(20F, Color.Blue),
        )
    )
)
