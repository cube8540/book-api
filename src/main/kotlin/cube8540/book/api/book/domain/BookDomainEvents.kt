package cube8540.book.api.book.domain

import org.springframework.context.ApplicationEvent

data class BookPostedEvent(val events: List<Book>): ApplicationEvent(events.map { it.isbn })