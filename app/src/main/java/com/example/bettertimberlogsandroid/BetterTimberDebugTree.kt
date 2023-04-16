import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import java.util.regex.Pattern

class BetterTimberDebugTree(private val globalTag: String = "GTAG") : Timber.DebugTree() {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonPattern: Pattern = Pattern.compile("(\\{(?:[^{}]|(?:\\{(?:[^{}]|(?:\\{[^{}]*\\}))*\\}))*\\})")

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
            if (element.className.contains("BetterTimberDebugTree")) {
                foundDebugTree = true
                false
            } else {
                foundDebugTree && !element.className.contains("Timber")
            }
        }
    }

    private fun formatJsonIfNeeded(message: String): String {
        val matcher = jsonPattern.matcher(message)
        val buffer = StringBuffer()

        while (matcher.find()) {
            try {
                val jsonAdapter: JsonAdapter<Any> = moshi.adapter(Any::class.java).indent("  ")
                val parsedObject = jsonAdapter.fromJson(matcher.group())
                val formattedJson = jsonAdapter.toJson(parsedObject)
                matcher.appendReplacement(buffer, formattedJson)
            } catch (e: Exception) {
                // Ignore and continue with the next JSON object
            }
        }

        matcher.appendTail(buffer)
        return buffer.toString()
    }
}
