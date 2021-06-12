package cube8540.book.api.converter

import java.net.URI
import javax.persistence.AttributeConverter

class UriToStringConverter: AttributeConverter<URI, String> {
    override fun convertToDatabaseColumn(attribute: URI?): String? = attribute?.toString()

    override fun convertToEntityAttribute(dbData: String?): URI? = dbData?.let { URI.create(it) }
}