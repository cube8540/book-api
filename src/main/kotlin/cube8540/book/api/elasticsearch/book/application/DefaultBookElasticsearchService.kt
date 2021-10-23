package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.elasticsearch.book.document.BookDocument
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service

@Service
class DefaultBookElasticsearchService @Autowired constructor(
    private val elasticsearchOperations: ElasticsearchOperations
): BookElasticsearchService {

    companion object {
        const val maxDocumentSize: Int = 10000
    }

    var maxDocumentSize: Int = DefaultBookElasticsearchService.maxDocumentSize

    override fun search(condition: BookElasticsearchCondition, pageable: Pageable): Page<BookDocument> {
        val maximumPage = ((maxDocumentSize - pageable.pageSize) / pageable.pageSize) + 1
        if (pageable.pageNumber > maximumPage) {
            throw DocumentPageInvalidException("Request page is grater then maximum page(${maximumPage})")
        }

        val queryBuilder = BoolQueryBuilder()
            .must(MatchQueryBuilder(BookDocument.titleFieldName, condition.title))

        if (condition.publishFrom != null && condition.publishTo != null) {
            queryBuilder.filter(RangeQueryBuilder(BookDocument.publishDateFieldName)
                .from(condition.publishFrom)
                .to(condition.publishTo))
        }

        if (condition.publisherCode != null) {
            queryBuilder.filter(MatchQueryBuilder(BookDocument.publisherCodeFieldName, condition.publisherCode))
        }

        return doSearch(NativeSearchQueryBuilder()
            .withQuery(queryBuilder).withPageable(pageable).build())
    }

    private fun doSearch(query: Query): Page<BookDocument> {
        return elasticsearchOperations.search(query, BookDocument::class.java)
            .let { PageImpl(it.searchHits.map { hit -> hit.content }.toList(), query.pageable, it.totalHits) }
    }
}