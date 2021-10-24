package cube8540.book.api.elasticsearch.book.domain

import cube8540.book.api.elasticsearch.book.document.BookDocument
import java.time.LocalDate

const val defaultIsbn = "isbn0000000"

const val defaultTitle = "title 00001"

val defaultPublishDate: LocalDate = LocalDate.of(2021, 11, 13)
val defaultPublishFrom: LocalDate = LocalDate.of(2021, 10, 23)
val defaultPublishTo: LocalDate = LocalDate.of(2021, 12, 31)

const val defaultPublisherCode = "publisher000000"

fun createBookDocument(
    isbn: String = defaultIsbn,
    title: String = defaultTitle,
    publishDate: LocalDate = defaultPublishDate,
    publisherCode: String = defaultPublisherCode
): BookDocument = BookDocument(isbn, title, publishDate, publisherCode)