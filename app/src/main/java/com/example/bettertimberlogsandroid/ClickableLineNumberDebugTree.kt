import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Buffer
import timber.log.Timber

class ClickableLineNumberDebugTree(private val globalTag: String = "GTAG") : Timber.DebugTree() {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        findLogCallStackTraceElement()?.let { element ->
            val lineNumberInfo = "(${element.fileName}:${element.lineNumber})"
            val formattedMessage = formatJsonIfNeeded(message)
            val updatedMessage = "$lineNumberInfo: $formattedMessage"
            super.log(priority, "$globalTag-$tag", updatedMessage, t)
        } ?: run {
            super.log(priority, "$globalTag-$tag", message, t)
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return element.fileName
    }

    private fun findLogCallStackTraceElement(): StackTraceElement? {
        val stackTrace = Throwable().stackTrace
        var foundDebugTree = false

        return stackTrace.firstOrNull { element ->
            if (element.className.contains("ClickableLineNumberDebugTree")) {
                foundDebugTree = true
                false
            } else {
                foundDebugTree && !element.className.contains("Timber")
            }
        }
    }

    private fun formatJsonIfNeeded(message: String): String {
        val jsonAdapter: JsonAdapter<Any> = moshi.adapter(Any::class.java).indent("  ")

        return try {
            val buffer = Buffer().writeUtf8(message)
            val jsonReader = JsonReader.of(buffer)
            val value = jsonAdapter.fromJson(jsonReader)
            jsonAdapter.toJson(value)
        } catch (e: Exception) {
            message
        }
    }
}
