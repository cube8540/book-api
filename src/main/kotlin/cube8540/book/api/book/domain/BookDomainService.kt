package cube8540.book.api.book.domain

import io.github.cube8540.validator.core.Validator

interface BookValidatorFactory {
    fun createValidator(book: Book): Validator<Book>
}