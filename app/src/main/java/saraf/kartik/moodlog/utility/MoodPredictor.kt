package saraf.kartik.moodlog.utility

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MoodPredictor(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val moodLabels = listOf("Angry", "Anxious", "Excited", "Happy", "Neutral", "Sad")

    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize MoodPredictor: ${e.message}", e)
        }
    }

    @Throws(Exception::class)
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd("mood_prediction_model.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(
            java.nio.channels.FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    fun predictMood(mood1: String, mood2: String, mood3: String): String {
        val standardizedMoods =
            listOf(mood1, mood2, mood3).map { it.replaceFirstChar { char -> char.uppercase() } }

        val encodedMoods = standardizedMoods.map { mood ->
            moodLabels.indexOf(mood).takeIf { it >= 0 }
                ?: throw IllegalArgumentException("Mood \"$mood\" is not recognized.")
        }

        val floatMoods = encodedMoods.map { it.toFloat() }

        val inputBuffer = ByteBuffer.allocateDirect(4 * 32 * 3).apply {
            order(ByteOrder.nativeOrder())

            repeat(32) {
                floatMoods.forEach { putFloat(it) }
            }
            rewind()
        }

        val outputBuffer =
            ByteBuffer.allocateDirect(6 * 4 * 32).apply {
                order(ByteOrder.nativeOrder())
            }

        return try {
            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer.rewind()

            val probabilities = FloatArray(6) { outputBuffer.float }

            val predictedIndex = probabilities.indices.maxByOrNull { probabilities[it] }
                ?: return "Unknown"

            moodLabels[predictedIndex]
        } catch (e: Exception) {
            "Prediction Error: ${e.message}"
        }
    }


    fun close() {
        interpreter?.close()
    }
}


