package cube8540.book.api.book.application

import io.github.cube8540.validator.core.ValidationError

data class BookPostErrorReason(
    var isbn: String,
    var errors: List<ValidationError>
)
