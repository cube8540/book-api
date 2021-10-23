package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.elasticsearch.book.document.BookDocument
import cube8540.book.api.elasticsearch.book.domain.defaultPublishFrom
import cube8540.book.api.elasticsearch.book.domain.defaultPublishTo
import cube8540.book.api.elasticsearch.book.domain.defaultPublisherCode
import cube8540.book.api.elasticsearch.book.domain.defaultTitle
import java.time.LocalDate
import kotlin.random.Random
import org.elasticsearch.search.aggregations.Aggregations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.data.elasticsearch.core.TotalHitsRelation
import org.springframework.data.elasticsearch.core.document.Explanation
import org.springframework.data.elasticsearch.core.document.NestedMetaData

fun createBookSearchCondition(
    title: String = defaultTitle,
    from: LocalDate? = defaultPublishFrom,
    to: LocalDate? = defaultPublishTo,
    publisherCode: String? = defaultPublisherCode
): BookElasticsearchCondition = BookElasticsearchCondition(title = title, publishFrom = from, publishTo = to, publisherCode = publisherCode)

fun createSearchHit(
    book: BookDocument,
    index: String = "books",
    routing: String? = null,
    score: Float = Random.nextFloat(),
    sortValues: Array<Any>? = null,
    highlightFields: Map<String, List<String>>? = null,
    innerHints: Map<String, SearchHits<Any>>? = null,
    nestedMetaData: NestedMetaData? = null,
    explanation: Explanation? = null,
    matchedQueries: List<String>? = null
): SearchHit<BookDocument> = SearchHit(
    index,
    book.isbn,
    routing,
    score,
    sortValues,
    highlightFields,
    innerHints,
    nestedMetaData,
    explanation,
    matchedQueries,
    book
)

fun createSearchHits(
    totalHits: Long = Random.nextLong(0, 100),
    totalHitsRelation: TotalHitsRelation = TotalHitsRelation.OFF,
    maxScore: Float = Random.nextFloat(),
    scrollId: String? = null,
    aggregations: Aggregations? = null,
    hits: List<SearchHit<BookDocument>>
): SearchHits<BookDocument> = SearchHitsImpl(totalHits, totalHitsRelation, maxScore, scrollId, hits, aggregations)