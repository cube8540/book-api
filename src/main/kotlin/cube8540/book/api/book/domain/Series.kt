package cube8540.book.api.book.domain

import java.io.Serializable
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Transient

@Embeddable
data class Series(
    @AttributeOverride(name = "value", column = Column(name = "series_isbn", length = 13))
    var isbn: Isbn? = null,

    @Column(name = "series_code", length = 32)
    var code: String? = null
): Serializable {

    @Transient
    fun isValid(): Boolean = isbn?.isValid() ?: true

    fun mergeSeries(series: Series) {
        if (series.isbn?.value?.isNotEmpty() == true) {
            this.isbn = series.isbn
        }
        if (series.code?.isNotEmpty() == true) {
            this.code = series.code
        }
    }
}