package cube8540.book.api.book.domain

import cube8540.book.api.BookApiApplication
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.Persistable
import java.time.Clock
import java.time.LocalDateTime
import javax.persistence.*
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "publishers")
class Publisher(codeGenerator: PublisherCodeGenerator): AbstractAggregateRoot<Publisher>(), Persistable<String> {

    companion object {
        internal var clock = Clock.system(BookApiApplication.DEFAULT_TIME_ZONE.toZoneId())

        fun fake(code: String) = Publisher(object: PublisherCodeGenerator {
            override fun generateCode(): String = code
        })
    }

    @Id
    @Column(name = "code")
    var code: String = codeGenerator.generateCode()

    @Column(name = "name", length = 32, nullable = false)
    var name: String? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @Transient
    var newObject: Boolean = true
        private set

    @PostLoad
    @PostPersist
    fun markingPersistedEntity() {
        this.newObject = false
    }

    override fun getId(): String = code

    override fun isNew(): Boolean = newObject

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        else -> other is Publisher && other.code == code
    }

    override fun hashCode(): Int = code.hashCode()
}