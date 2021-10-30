package cube8540.book.api.book.domain

import cube8540.book.api.converter.UriToStringConverter
import java.io.Serializable
import java.net.URI
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
data class BookExternalLink(
    @Convert(converter = UriToStringConverter::class)
    @Column(name = "product_detail_page", length = 248, nullable = false)
    var productDetailPage: URI,

    @Column(name = "original_price", nullable = true)
    var originalPrice: Double? = null,

    @Column(name = "sale_price", nullable = true)
    var salePrice: Double? = null
): Serializable