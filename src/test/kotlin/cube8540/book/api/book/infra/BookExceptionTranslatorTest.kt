package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.domain.BookNotFoundException
import cube8540.book.api.error.ErrorCodes
import cube8540.book.api.error.ErrorMessage
import io.github.cube8540.validator.core.ValidationError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class BookExceptionTranslatorTest {

    private val translator = BookExceptionTranslator()

    @Test
    fun `translate book invalid exception`() {
        val errors: List<ValidationError> = emptyList()
        val invalidException = BookInvalidException.instance(errors)

        val result = translator.translate(invalidException)

        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body!!.errorCode).isEqualTo(ErrorCodes.INVALID_REQUEST)
        assertThat((result.body!!.description as Collection<*>)).containsAll(errors)
    }

    @Test
    fun `translate book not found exception`() {
        val errorMessage = "test not found exception"
        val notFoundException = BookNotFoundException.instance(errorMessage)

        val result = translator.translate(notFoundException)

        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(result.body!!.errorCode).isEqualTo(ErrorCodes.NOT_FOUND)
        assertThat(result.body!!.description).isEqualTo(errorMessage)
    }

    @Test
    fun `translate unknown exception`() {
        val exception = Exception()

        val result = translator.translate(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(result.body).isEqualTo(ErrorMessage.UNKNOWN_SERVER_ERROR)
    }

}