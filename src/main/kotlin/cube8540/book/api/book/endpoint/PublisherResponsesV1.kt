package cube8540.book.api.book.endpoint

import java.time.LocalDateTime

data class PublisherResponseV1(
    var code: String,
    var name: String,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?
)