import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import timber.log.Timber

class ClickableLineNumberDebugTree(private val globalTag: String = "GTAG") : Timber.DebugTree() {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

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
        return try {
            val jsonElement = JsonParser.parseString(message)
            gson.toJson(jsonElement)
        } catch (e: Exception) {
            message
        }
    }
}
