package cube8540.book.api.book.infra

import cube8540.book.api.book.application.BookExternalLinkPostRequest
import cube8540.book.api.book.application.BookPostRequest
import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookExternalLink
import cube8540.book.api.book.domain.BookInitializer
import cube8540.book.api.book.domain.BookThumbnail
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Series

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
        book.indexes = register.indexes
        book.confirmedPublication = register.confirmedPublication ?: false
        book.externalLinks = register.externalLinks
            ?.mapValues { it -> convertExternalLink(it.value) }?.toMutableMap()
    }

    private fun convertExternalLink(externalLink: BookExternalLinkPostRequest): BookExternalLink =
        BookExternalLink(externalLink.productDetailPage, externalLink.originalPrice, externalLink.salePrice)
}