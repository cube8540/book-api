package cube8540.book.api.error

import io.github.cube8540.validator.core.ValidationError
import io.github.cube8540.validator.core.exception.ValidateException
import java.lang.RuntimeException

open class ServiceException(val code: String, message: String): RuntimeException(message)

open class ServiceInvalidException(val code: String, errors: Array<ValidationError>): ValidateException(* errors) {
    constructor(code: String, errors: Collection<ValidationError>): this(code, errors.toTypedArray())
}