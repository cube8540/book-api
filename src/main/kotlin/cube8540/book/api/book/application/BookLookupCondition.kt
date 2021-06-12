package cube8540.book.api.book.application

import org.springframework.data.domain.Sort
import java.time.LocalDate

data class BookLookupCondition(
    var publishFrom: LocalDate? = null,

    var publishTo: LocalDate? = null,

    var seriesIsbn: String? = null,

    var seriesCode: String? = null,

    var publisherCode: String? = null,

    var direction: Sort.Direction = Sort.Direction.DESC,
)