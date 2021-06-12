package cube8540.book.api.book.repository

import java.time.LocalDate

data class BookQueryCondition(
    var publishFrom: LocalDate? = null,
    var publishTo: LocalDate? = null,
    var publisherCode: String? = null,
    var seriesIsbn: String? = null,
    var seriesCode: String? = null
)