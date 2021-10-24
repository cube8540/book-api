package cube8540.book.api.elasticsearch.book.application

import java.time.LocalDate

data class BookElasticsearchCondition(
    var publishFrom: LocalDate? = null,

    var publishTo: LocalDate? = null,

    var publisherCode: String? = null,

    var title: String
)