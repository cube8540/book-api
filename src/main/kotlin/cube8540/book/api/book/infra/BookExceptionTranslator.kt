package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.domain.BookNotFoundException
import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class BookExceptionTranslator: ExceptionTranslator<ErrorMessage<Any>> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun translate(exception: Exception): ResponseEntity<ErrorMessage<Any>> = when (exception) {
        is BookInvalidException -> {
            response(HttpStatus.BAD_REQUEST, ErrorMessage.instance(exception.code, exception.errors))
        }
        is BookNotFoundException -> {
            response(HttpStatus.NOT_FOUND, ErrorMessage.instance(exception.code, exception.message))
        }
        else -> {
            logger.error("Handle exception {} {}", exception.javaClass, exception.message)
            response(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UNKNOWN_SERVER_ERROR)
        }
    }
}