package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Publisher
import java.time.LocalDateTime

data class PublisherDetails(
    var code: String,
    var name: String? = null,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(publisher: Publisher) = PublisherDetails(
            code = publisher.code,
            name = publisher.name,
            createdAt = publisher.createdAt,
            updatedAt = publisher.updatedAt
        )
    }
}
