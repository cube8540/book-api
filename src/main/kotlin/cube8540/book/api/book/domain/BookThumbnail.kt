package cube8540.book.api.book.domain

import cube8540.book.api.converter.UriToStringConverter
import java.io.Serializable
import java.net.URI
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
data class BookThumbnail(
    @Convert(converter = UriToStringConverter::class)
    @Column(name = "large_thumbnail", length = 128)
    var largeThumbnail: URI? = null,

    @Convert(converter = UriToStringConverter::class)
    @Column(name = "medium_thumbnail", length = 128)
    var mediumThumbnail: URI? = null,

    @Convert(converter = UriToStringConverter::class)
    @Column(name = "small_thumbnail", length = 128)
    var smallThumbnail: URI? = null
): Serializable {

    fun mergeThumbnail(thumbnail: BookThumbnail) {
        thumbnail.largeThumbnail?.let { this.largeThumbnail = it }
        thumbnail.mediumThumbnail?.let { this.mediumThumbnail = it }
        thumbnail.smallThumbnail?.let { this.smallThumbnail = it }
    }
}