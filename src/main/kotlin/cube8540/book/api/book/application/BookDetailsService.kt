package cube8540.book.api.book.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BookDetailsService {

    fun lookupBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetails>

}