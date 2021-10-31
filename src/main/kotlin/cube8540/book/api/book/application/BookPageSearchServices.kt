package cube8540.book.api.book.application

import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

const val MAXIMUM_LOOKUP_PAGE = 5

data class BookLookupCondition(
    var publishFrom: LocalDate? = null,

    var publishTo: LocalDate? = null,

    var seriesIsbn: String? = null,

    var seriesCode: String? = null,

    var publisherCode: String? = null,

    var title: String? = null,

    var direction: Sort.Direction = Sort.Direction.DESC,
)

interface BookPageSearchService {
    fun searchBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetail>
}

interface BookLookupService {
    fun lookupForNewestBook(pageable: Pageable): Page<BookDetail>

    fun lookupForLatestUpdate(pageable: Pageable): Page<BookDetail>

    fun lookupForPublishedToday(pageable: Pageable): Page<BookDetail>
}