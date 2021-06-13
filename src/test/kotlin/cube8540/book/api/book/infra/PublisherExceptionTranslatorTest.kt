package cube8540.book.api.book.infra

import cube8540.book.api.error.ErrorMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class PublisherExceptionTranslatorTest {

    private val translator = PublisherExceptionTranslator()

    @Test
    fun `translate unknown exception`() {
        val exception = Exception()

        val result = translator.translate(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(result.body).isEqualTo(ErrorMessage.UNKNOWN_SERVER_ERROR)
    }
}