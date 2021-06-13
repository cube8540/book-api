package cube8540.book.api.book.infra

import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class PublisherExceptionTranslator: ExceptionTranslator<ErrorMessage<Any>> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun translate(exception: Exception): ResponseEntity<ErrorMessage<Any>> {
        logger.error("Handle exception {} {}", exception.javaClass, exception.message)
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UNKNOWN_SERVER_ERROR)
    }
}