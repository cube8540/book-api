package cube8540.book.api

import com.fasterxml.jackson.core.SerializableString
import com.fasterxml.jackson.core.io.CharacterEscapes
import com.fasterxml.jackson.core.io.SerializedString
import org.apache.commons.text.StringEscapeUtils

class HtmlCharacterEscapes: CharacterEscapes() {

    private val asciiEscapes: IntArray = standardAsciiEscapesForJSON()

    init {
        asciiEscapes['<'.code] = ESCAPE_CUSTOM
        asciiEscapes['>'.code] = ESCAPE_CUSTOM
        asciiEscapes['\"'.code] = ESCAPE_CUSTOM
        asciiEscapes['('.code] = ESCAPE_CUSTOM
        asciiEscapes[')'.code] = ESCAPE_CUSTOM
        asciiEscapes['#'.code] = ESCAPE_CUSTOM
        asciiEscapes['\''.code] = ESCAPE_CUSTOM
    }

    override fun getEscapeCodesForAscii(): IntArray = asciiEscapes

    override fun getEscapeSequence(ch: Int): SerializableString = SerializedString(StringEscapeUtils
        .escapeHtml4(Character.toString(ch)))
}