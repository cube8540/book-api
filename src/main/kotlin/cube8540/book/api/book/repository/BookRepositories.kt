package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Series
import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

data class BookQueryCondition(
    var publishFrom: LocalDate? = null,
    var publishTo: LocalDate? = null,
    var publisherCode: String? = null,
    var seriesIsbn: String? = null,
    var seriesCode: String? = null
)

interface BookCustomRepository {
    fun findDetailsByIsbn(isbn: Isbn): Book?

    fun findSeries(series: Series): List<Book>

    fun findDetailsByIsbn(isbnList: Collection<Isbn>): List<Book>

    fun findPageByCondition(condition: BookQueryCondition, pageRequest: PageRequest): Page<Book>

    fun findByPublishDate(date: LocalDate, pageRequest: PageRequest): Page<Book>

    fun findAll(pageRequest: PageRequest): Page<Book>
}

interface BookRepository: JpaRepository<Book, Isbn>, BookCustomRepository