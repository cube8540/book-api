package cube8540.book.api.book.domain

import cube8540.book.api.BookApiApplication
import io.github.cube8540.validator.core.ValidationResult
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostUpdate
import javax.persistence.Table
import javax.persistence.Transient
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.Persistable

@Entity
@Table(name = "books")
@DynamicInsert
@DynamicUpdate
class Book(

    @EmbeddedId
    var isbn: Isbn,

    @Column(name = "title", length = 256)
    var title: String,

    @Column(name = "publish_date")
    var publishDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_code", nullable = false)
    var publisher: Publisher

): AbstractAggregateRoot<Book>(), Persistable<Isbn> {

    companion object {
        internal var clock = Clock.system(BookApiApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @Embedded
    var series: Series? = null

    @Embedded
    var thumbnail: BookThumbnail? = null

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 500)
    @CollectionTable(name = "book_authors", joinColumns = [JoinColumn(name = "isbn")])
    @Column(name = "author", length = 32, nullable = false)
    var authors: MutableSet<String>? = null

    @Lob
    @Column(name = "description", columnDefinition = "text")
    var description: String? = null

    @Column(name = "price")
    var price: Double? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(clock)

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(clock)

    @Transient
    var newObject: Boolean = true
        private set

    @Transient
    var persistCompleted: Boolean = false
        private set

    @Transient
    var mergeCompleted: Boolean = false
        private set

    @PostLoad
    fun markingLoadOnRepository() {
        this.newObject = false
    }

    @PostPersist
    fun markingPersistedEntity() {
        this.newObject = false
        this.persistCompleted = true
    }

    @PostUpdate
    fun markingMergedEntity() {
        this.mergeCompleted = true
    }

    fun mergeBook(book: Book) {
        this.title = book.title
        this.publishDate = book.publishDate

        book.description?.let { this.description = it }
        book.price?.let { this.price = it }

        if (this.series != null && book.series != null) {
            this.series!!.mergeSeries(book.series!!)
        } else if (book.series != null) {
            this.series = book.series
        }

        if (this.thumbnail != null && book.thumbnail != null) {
            this.thumbnail!!.mergeThumbnail(book.thumbnail!!)
        } else if (book.thumbnail != null) {
            this.thumbnail = book.thumbnail
        }

        if (this.authors != null && book.authors != null && book.authors!!.isNotEmpty()) {
            this.authors!!.addAll(book.authors!!)
        } else if (book.authors != null && book.authors!!.isNotEmpty()) {
            this.authors = book.authors
        }

        this.updatedAt = LocalDateTime.now(clock)
    }

    @Transient
    fun isValid(validatorFactory: BookValidatorFactory) {
        validatorFactory.createValidator(this).result.hasErrorThrows { BookInvalidException.instance(it) }
    }

    fun validationResult(validatorFactory: BookValidatorFactory): ValidationResult =
        validatorFactory.createValidator(this).result

    override fun getId(): Isbn = isbn

    override fun isNew(): Boolean = newObject

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        else -> other is Book && other.isbn == isbn
    }

    override fun hashCode(): Int = isbn.hashCode()
}