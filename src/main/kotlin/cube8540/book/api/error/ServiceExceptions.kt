package cube8540.book.api.error

import io.github.cube8540.validator.core.ValidationError
import io.github.cube8540.validator.core.exception.ValidateException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.lang.RuntimeException

open class ServiceException(val code: String, message: String): RuntimeException(message)

open class ServiceInvalidException(val code: String, errors: Array<ValidationError>): ValidateException(* errors) {
    constructor(code: String, errors: Collection<ValidationError>): this(code, errors.toTypedArray())
}

interface ExceptionTranslator<T> {
    fun <B> response(status: HttpStatus, body: B): ResponseEntity<B> = ResponseEntity(body, status)

    fun translate(exception: Exception): ResponseEntity<T>
}