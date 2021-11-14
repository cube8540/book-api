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
import javax.persistence.EnumType
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn
import javax.persistence.MapKeyEnumerated
import javax.persistence.OrderColumn
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostUpdate
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
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

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 500)
    @CollectionTable(name = "book_indexes", joinColumns = [JoinColumn(name = "isbn")])
    @Column(name = "title", length = 128, nullable = false)
    @OrderColumn(name = "odr")
    var indexes: MutableList<String>? = null

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 500)
    @CollectionTable(name = "book_external_links", joinColumns = [JoinColumn(name = "isbn")])
    @MapKeyColumn(name = "mapping_type", length = 32, nullable = false)
    @MapKeyEnumerated(EnumType.STRING)
    var externalLinks: MutableMap<MappingType, BookExternalLink>? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(clock)

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(clock)

    @Column(name = "confirmed_publication", nullable = false)
    var confirmedPublication: Boolean = false

    @Transient
    var newObject: Boolean = true
        private set

    @PostLoad
    @PostPersist
    @PostUpdate
    fun markingLoadOnRepository() {
        this.newObject = false
    }

    @PreUpdate
    @PrePersist
    fun markingLastUpdatedAt() {
        this.updatedAt = LocalDateTime.now(clock)
        if (domainEvents().none { it is BookPostedEvent }) {
            registerEvent(BookPostedEvent(isbn, updatedAt))
        }
    }

    fun mergeBook(book: Book) {
        this.title = book.title
        this.publishDate = book.publishDate
        this.description = book.description
        this.confirmedPublication = book.confirmedPublication

        if (book.indexes != null && this.indexes != book.indexes) {
            this.indexes = book.indexes
        }

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

        if (this.authors != null && book.authors != null && this.authors != book.authors) {
            this.authors!!.addAll(book.authors!!)
            this.markingLastUpdatedAt()
        } else if (this.authors != book.authors && book.authors != null) {
            this.authors = book.authors
        }

        if (this.externalLinks != null && book.externalLinks != null && this.externalLinks != book.externalLinks) {
            this.externalLinks!!.putAll(book.externalLinks!!)
            this.markingLastUpdatedAt()
        } else if (this.externalLinks != book.externalLinks && book.externalLinks != null) {
            this.externalLinks = book.externalLinks
        }
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