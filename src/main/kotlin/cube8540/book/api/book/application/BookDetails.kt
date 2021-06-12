package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookThumbnail
import cube8540.book.api.book.domain.Series
import java.time.LocalDate
import java.time.LocalDateTime

data class BookDetails(
    val isbn: String,
    val title: String,
    val publisher: PublisherDetails,
    val publishDate: LocalDate,
    val series: Series? = null,
    val thumbnail: BookThumbnail? = null,
    val authors: Set<String>? = null,
    val description: String? = null,
    val price: Double? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(book: Book): BookDetails = BookDetails(
            isbn = book.isbn.value,
            title = book.title,
            publisher = PublisherDetails.of(book.publisher),
            publishDate = book.publishDate,
            series = book.series,
            thumbnail = book.thumbnail,
            authors = book.authors?.map { it }?.toSet(),
            description = book.description,
            price = book.price,
            createdAt = book.createdAt,
            updatedAt = book.updatedAt
        )
    }
}