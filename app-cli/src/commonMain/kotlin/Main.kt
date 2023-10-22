package opensavvy.notes.cli

import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.textLine

fun main() = session {
	section { textLine("Hello world!") }.run()
}
