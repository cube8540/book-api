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
    var largeThumbnail: URI?,

    @Convert(converter = UriToStringConverter::class)
    @Column(name = "medium_thumbnail", length = 128)
    var mediumThumbnail: URI?,

    @Convert(converter = UriToStringConverter::class)
    @Column(name = "small_thumbnail", length = 128)
    var smallThumbnail: URI?
): Serializable