package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.domain.defaultTitle
import java.time.LocalDate

val defaultPublishFrom: LocalDate = LocalDate.of(2021, 1, 1)
val defaultPublishTo: LocalDate = LocalDate.of(2021, 12, 31)

fun createExternalSearchRequest(
    title: String = defaultTitle,
    from: LocalDate = defaultPublishFrom,
    to: LocalDate = defaultPublishTo,
    publisherCode: String = defaultPublisherCode
) = BookExternalSearchRequest(title = title, publishFrom = from, publishTo = to, publisherCode = publisherCode)