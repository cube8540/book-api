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
    override fun searchBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetail> {
        return if (condition.title != null) {
            applicationContext.getBean(BookPageExternalSearchService::class.java).searchBooks(condition, pageable)
        } else {
            applicationContext.getBean(ApplicationBookService::class.java).searchBooks(condition, pageable)
        }
    }
}