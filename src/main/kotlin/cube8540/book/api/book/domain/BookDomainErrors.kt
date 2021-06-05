package cube8540.book.api.book.domain

import cube8540.book.api.error.ErrorCodes
import cube8540.book.api.error.ServiceInvalidException
import io.github.cube8540.validator.core.ValidationError

class BookInvalidException(code: String, errors: List<ValidationError>): ServiceInvalidException(code, errors) {
    companion object {
        fun instance(errors: List<ValidationError>): BookInvalidException =
            BookInvalidException(ErrorCodes.INVALID_REQUEST, errors)
    }
}