package cube8540.book.api.book.domain

import cube8540.book.api.BookApiApplication
import java.time.Clock
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "publishers")
class Publisher(codeGenerator: PublisherCodeGenerator) {

    companion object {
        internal var clock = Clock.system(BookApiApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @Id
    @Column(name = "code")
    var code: String = codeGenerator.generateCode()

    @Column(name = "name", length = 32, nullable = false)
    var name: String? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(clock)

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        else -> other is Publisher && other.code == code
    }

    override fun hashCode(): Int = code.hashCode()
}