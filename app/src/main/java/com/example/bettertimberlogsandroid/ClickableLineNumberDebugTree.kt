import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import timber.log.Timber
import java.util.regex.Pattern

class ClickableLineNumberDebugTree(private val globalTag: String = "GTAG") : Timber.DebugTree() {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
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
            if (element.className.contains("ClickableLineNumberDebugTree")) {
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
                val jsonElement = JsonParser.parseString(matcher.group())
                val formattedJson = gson.toJson(jsonElement)
                matcher.appendReplacement(buffer, formattedJson)
            } catch (e: Exception) {
                // Ignore and continue with the next JSON object
            }
        }

        matcher.appendTail(buffer)
        return buffer.toString()
    }
}
