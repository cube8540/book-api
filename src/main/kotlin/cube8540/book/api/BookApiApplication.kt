package cube8540.book.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import java.time.Clock
import java.time.ZoneOffset
import java.util.*

@SpringBootApplication
@EnableAspectJAutoProxy
class BookApiApplication {
    companion object {
        val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+09:00")
        val DEFAULT_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val DEFAULT_CLOCK: Clock = Clock.system(DEFAULT_TIME_ZONE.toZoneId())
    }
}

fun main(args: Array<String>) {
    runApplication<BookApiApplication>(*args)
}

