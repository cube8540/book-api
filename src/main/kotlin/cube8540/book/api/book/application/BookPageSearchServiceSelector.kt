package cube8540.book.api.book.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookPageSearchServiceSelector @Autowired constructor(
    private val applicationContext: ApplicationContext
): BookPageSearchService {
    override fun lookupBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetails> {
        return if (condition.title != null) {
            applicationContext.getBean(BookPageExternalSearchService::class.java).lookupBooks(condition, pageable)
        } else {
            applicationContext.getBean(ApplicationBookService::class.java).lookupBooks(condition, pageable)
        }
    }
}