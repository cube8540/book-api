package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookExternalLink
import cube8540.book.api.book.domain.BookThumbnail
import cube8540.book.api.book.domain.MappingType
import cube8540.book.api.book.domain.Series
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

data class BookExternalLinkDetail(
    val productDetailPage: URI,
    val originalPrice: Double? = null,
    val salePrice: Double? = null
) {
    companion object {
        fun of(externalLink: BookExternalLink): BookExternalLinkDetail =
            BookExternalLinkDetail(externalLink.productDetailPage, externalLink.originalPrice, externalLink.salePrice)
    }
}

data class BookDetail(
    val isbn: String,
    val title: String,
    val publisher: PublisherDetails,
    val publishDate: LocalDate,
    val series: Series? = null,
    val thumbnail: BookThumbnail? = null,
    val authors: Set<String>? = null,
    val description: String? = null,
    val indexes: List<String>? = null,
    val externalLinks: Map<MappingType, BookExternalLinkDetail>? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(book: Book): BookDetail = BookDetail(
            isbn = book.isbn.value,
            title = book.title,
            publisher = PublisherDetails.of(book.publisher),
            publishDate = book.publishDate,
            series = book.series,
            thumbnail = book.thumbnail,
            authors = book.authors?.map { it }?.toSet(),
            description = book.description,
            indexes = book.indexes?.toList(),
            externalLinks = book.externalLinks?.mapValues { BookExternalLinkDetail.of(it.value) },
            createdAt = book.createdAt,
            updatedAt = book.updatedAt
        )

        fun withoutCollection(book: Book): BookDetail = BookDetail(
            isbn = book.isbn.value,
            title = book.title,
            publisher = PublisherDetails.of(book.publisher),
            publishDate = book.publishDate,
            series = book.series,
            thumbnail = book.thumbnail,
            description = book.description,
            createdAt = book.createdAt,
            updatedAt = book.updatedAt
        )
    }
}