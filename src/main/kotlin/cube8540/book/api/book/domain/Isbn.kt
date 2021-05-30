package cube8540.book.api.book.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Transient

@Embeddable
data class Isbn(
    @Column(name = "isbn", length = 13, nullable = false)
    var value: String
): Serializable {

    companion object {
        private val regex = Regex("^[0-9]{13}\$")
        private val weights = intArrayOf(1, 3)
    }

    init {
        this.value = this.value.replace("-", "")
    }

    /**
     * ISBN의 체크 번호를 반환한다.
     *
     * 자세한 것은 [서지정보유통지원시스템](http://seoji.nl.go.kr/front/service/isbn_info09.jsp)를 참고
     *
     * @return ISBN 체크 번호
     */
    @Transient
    fun getCheckNumber(): Int = value.toCharArray().last().digitToInt()

    /**
     * ISBN의 유효성을 검사한다.
     * 검사 방법은 ISBN 12자리에 1과 3을 번갈아 곱하여 곱한 값을 합한 후 10으로 나누어
     * 나눈 나머지 값을 10에서 뺀 값이 체크 기호화 같아야 한다.
     *
     * 자세한 유효성 검사 방법은 [서지정보유통지원시스템](http://seoji.nl.go.kr/front/service/isbn_info09.jsp)를 참고
     * 
     * @return ISBN 유효 여부
     */
    @Transient
    fun isValid(): Boolean = matchPattern() && matchCheckNumber()

    private fun matchPattern() = value.matches(regex)

    private fun matchCheckNumber(): Boolean {
        val mod = getWeightedSum() % 10
        return if (mod == 0) {
            getCheckNumber() == 0
        } else {
            (10 - mod) == getCheckNumber()
        }
    }

    @Transient
    private fun getWeightedSum(): Int {
        val charArray = value.toCharArray()
        return IntRange(0, value.length - 2)
            .sumOf { charArray[it].digitToInt() * weights[it % 2] }
    }
}