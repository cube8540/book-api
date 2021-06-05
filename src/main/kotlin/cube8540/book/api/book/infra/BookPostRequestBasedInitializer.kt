package cube8540.book.api.book.infra

import cube8540.book.api.book.application.BookPostRequest
import cube8540.book.api.book.domain.*

class BookPostRequestBasedInitializer(private val register: BookPostRequest): BookInitializer {

    override fun initializingBook(book: Book) {
        if (register.seriesIsbn != null || register.seriesCode != null) {
            book.series = Series(isbn = register.seriesIsbn?.let { Isbn(it) }, register.seriesCode)
        } else {
            book.series = null
        }

        if (register.largeThumbnail != null || register.mediumThumbnail != null || register.smallThumbnail != null) {
            book.thumbnail = BookThumbnail(register.largeThumbnail, register.mediumThumbnail, register.smallThumbnail)
        } else {
            book.thumbnail = null
        }

        book.authors = register.authors
        book.description = register.description
        book.price = register.price
    }
}