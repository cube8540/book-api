package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookValidatorFactory
import cube8540.book.api.book.repository.PublisherContainer
import cube8540.book.api.book.repository.PublisherContainerHolder
import io.github.cube8540.validator.core.ValidationError
import io.github.cube8540.validator.core.ValidationRule
import io.github.cube8540.validator.core.Validator
import org.springframework.stereotype.Component

class DefaultBookIsbnValidationRule(private val property: String, private val message: String): ValidationRule<Book> {

    companion object {
        private const val defaultProperty = "isbn"
        private const val defaultMessage = "ISBN 형식이 옳바르지 않습니다."
    }

    constructor(): this(defaultProperty, defaultMessage)

    override fun isValid(target: Book): Boolean = target.isbn.isValid()

    override fun error(): ValidationError = ValidationError(property, message)

}

class DefaultBookPublisherValidationRule(private val property: String, private val message: String): ValidationRule<Book> {

    companion object {
        private const val defaultProperty = "publisherCode"
        private const val defaultMessage = "등록 되지 않은 출판사 입니다."
    }

    lateinit var container: PublisherContainer

    constructor(): this(defaultProperty, defaultMessage)

    override fun isValid(target: Book): Boolean = container.getPublisher(target.publisher.code)?.let { true } ?: false

    override fun error(): ValidationError = ValidationError(property, message)
}

class DefaultBookSeriesValidationRule(private val property: String, private val message: String): ValidationRule<Book> {

    companion object {
        private const val defaultProperty = "seriesIsbn"
        private const val defaultMessage = "ISBN 형식이 옳바르지 않습니다."
    }

    constructor(): this(defaultProperty, defaultMessage)

    override fun isValid(target: Book): Boolean = target.series?.isValid() ?: true

    override fun error(): ValidationError = ValidationError(property, message)

}

@Component
class DefaultBookValidatorFactory: BookValidatorFactory {

    override fun createValidator(book: Book): Validator<Book> {
        val publisherValidationRule = DefaultBookPublisherValidationRule()
        publisherValidationRule.container = PublisherContainerHolder.getContainer()

        return Validator.of(book).registerRule(DefaultBookIsbnValidationRule())
            .registerRule(DefaultBookSeriesValidationRule())
            .registerRule(publisherValidationRule)
    }
}