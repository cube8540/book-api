package cube8540.book.api

import java.time.Instant
import java.time.LocalDate

fun LocalDate.toDefaultInstant(): Instant = this.atStartOfDay().toInstant(BookApiApplication.DEFAULT_ZONE_OFFSET)