package cube8540.book.api.book.domain

import cube8540.book.api.BookApiApplication
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.Persistable
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

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

    @PostLoad
    fun markingPersistedEntity() {
        this.newObject = false
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

    override fun getId(): Isbn = isbn

    override fun isNew(): Boolean = newObject

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        else -> other is Book && other.isbn == isbn
    }

    override fun hashCode(): Int = isbn.hashCode()
}