package cube8540.book.api.book.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * isbn 유효성 검사는 [서지정보유통지원시스템](http://seoji.nl.go.kr/front/service/isbn_info09.jsp)를 참고
 */
internal class IsbnTest {

    private val isbnWithHyphen = "979-11-362-0209-3"
    private val isbnWithoutHyphen = "9791136202093"
    private val isbnIncludeNotNumberCharacter = "979116412012A"
    private val isbnWrongCheckNumber = "9791164120121"
    private val isbnChecksumMod0 = "9791127856410"

    private val charPool: CharRange = ('0'..'9')

    @Test
    fun `initialization with hyphen`() {
        val isbn = Isbn(isbnWithHyphen)

        assertThat(isbn.value).isEqualTo(isbnWithoutHyphen)
    }

    @ParameterizedTest
    @MethodSource(value = ["isbnWithCheckNumberProvider"])
    fun `isbn check number`(givenIsbn: String, checkNumber: Int) {
        val isbn = Isbn(givenIsbn)

        assertThat(isbn.getCheckNumber()).isEqualTo(checkNumber)
    }

    @ParameterizedTest
    @MethodSource(value = ["isbnLengthLessThen13Provider", "isbnLengthGreaterThen13Provider"])
    fun `validation when isbn length not allowed`(givenIsbn: String) {
        val isbn = Isbn(givenIsbn)

        assertThat(isbn.isValid()).isFalse
    }

    @Test
    fun `validation when isbn include not number character`() {
        val isbn = Isbn(isbnIncludeNotNumberCharacter)

        assertThat(isbn.isValid()).isFalse
    }

    @Test
    fun `validation when isbn not matched check number`() {
        val isbn = Isbn(isbnWrongCheckNumber)

        assertThat(isbn.isValid()).isFalse
    }

    @Test
    fun `validation when isbn checksum mod 0`() {
        val isbn = Isbn(isbnChecksumMod0)

        assertThat(isbn.isValid()).isTrue
    }

    @Test
    fun `validation when isbn is valid`() {
        val isbn = Isbn(isbnWithHyphen)

        assertThat(isbn.isValid()).isTrue
    }

    fun isbnWithCheckNumberProvider() = Stream.of(
        Arguments.of("9791136202093", 3),
        Arguments.of("9791136202091", 1),
        Arguments.of("9791136202092", 2),
        Arguments.of("9791136202095", 5),
        Arguments.of("9791136202096", 6),
    )

    fun isbnLengthLessThen13Provider(): Stream<Arguments> {
        val results = ArrayList<String>()
        for (i in 1 until 13) {
            val isbn = (1..i).map { charPool.random() }.joinToString("")
            results.add(isbn)
        }
        return results.map { Arguments.of(it) }.stream()
    }

    fun isbnLengthGreaterThen13Provider(): Stream<Arguments> {
        val isbn = (1..(14))
            .map { charPool.random() }
            .joinToString("")
        return Stream.of(Arguments.of(isbn))
    }
}