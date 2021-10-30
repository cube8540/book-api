package cube8540.book.api.book.domain

import java.io.Serializable
import java.time.LocalDateTime

data class BookPostedEvent(val isbn: Isbn, val issuedAt: LocalDateTime): Serializable