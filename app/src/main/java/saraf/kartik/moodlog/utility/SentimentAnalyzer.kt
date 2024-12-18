package saraf.kartik.moodlog.utility

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Delegate
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.flex.FlexDelegate
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.absoluteValue

class SentimentAnalyzer(private val context: Context) {
    private var interpreter: Interpreter? = null

    init {
        val options = Interpreter.Options()
        val flexDelegate: Delegate = FlexDelegate()
        options.addDelegate(flexDelegate)
        interpreter = Interpreter(loadModelFile(), options)
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor =
            context.assets.openFd("mood_analysis_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    private fun tokenizeText(text: String): ByteBuffer {
        val tokens = text.removePrefix("\"").removeSuffix("\"").split(" ").take(100).map { (it.hashCode() % 10000).toFloat().absoluteValue }
        val paddedTokens = tokens + List(100 - tokens.size) { 0f }
        val buffer = ByteBuffer.allocateDirect(4 * 100).apply {
            order(ByteOrder.nativeOrder())
            paddedTokens.forEach { (putFloat(it)) }
        }
        return buffer
    }

    fun classifyMood(text: String): String {
        val tokenizedInput = tokenizeText(text)
        val inputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 100), DataType.FLOAT32)
        inputTensor.loadBuffer(tokenizedInput)
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
        interpreter?.run(inputTensor.buffer, outputTensor.buffer)
        val outputArray = outputTensor.floatArray
        Log.i("SentimentAnalyzer", "Predictions: ${outputArray.contentToString()}")
        val predictedIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
        return when (predictedIndex) {
            0 -> "Angry"
            1 -> "Anxious"
            2 -> "Excited"
            3 -> "Happy"
            4 -> "Neutral"
            5 -> "Sad"
            else -> "Unknown"
        }
    }

    fun close() {
        interpreter?.close()
    }
}
