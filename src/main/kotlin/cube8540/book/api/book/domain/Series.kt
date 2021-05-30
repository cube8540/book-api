package cube8540.book.api.book.domain

import java.io.Serializable
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Transient

@Embeddable
data class Series(
    @AttributeOverride(name = "value", column = Column(name = "series_isbn", length = 13))
    var isbn: Isbn?,

    @Column(name = "series_code", length = 32)
    var code: String?
): Serializable {

    @Transient
    fun isValid(): Boolean = isbn?.isValid() ?: true
}