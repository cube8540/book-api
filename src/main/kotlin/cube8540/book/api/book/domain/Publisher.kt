package cube8540.book.api.book.domain

import cube8540.book.api.BookApiApplication
import java.time.Clock
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Table
import javax.persistence.Transient
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.Persistable

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

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @Transient
    var newObject: Boolean = true
        private set

    @PrePersist
    fun setCreatedAt() {
        this.createdAt = LocalDateTime.now(Book.clock)
        this.updatedAt = LocalDateTime.now(Book.clock)
    }

    @PreUpdate
    fun setUpdatedAt() {
        this.updatedAt = LocalDateTime.now(Book.clock)
    }

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