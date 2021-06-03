package cube8540.book.api.book.domain

import cube8540.book.api.error.ErrorCodes
import cube8540.book.api.error.ServiceException

class PublisherNotFoundException(code: String, message: String): ServiceException(code, message) {
    companion object {
        fun instance(message: String): PublisherNotFoundException =
            PublisherNotFoundException(ErrorCodes.NOT_FOUND, message)
    }
}