package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.error.ErrorCodes
import cube8540.book.api.error.ServiceException

class DocumentPageInvalidException(message: String): ServiceException(ErrorCodes.INVALID_REQUEST, message)