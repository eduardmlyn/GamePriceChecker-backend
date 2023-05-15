package cz.muni.fi.gamepricecheckerbackend.util

import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.text.ParseException
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.util.Date
import java.util.Locale


@Component
class DateParser(private val logger: Logger) {
    private val formatterDaySimple = DateTimeFormatter.ofPattern("d MMM, yyyy")
    private val formatterDayLess = DateTimeFormatterBuilder()
        .parseDefaulting(ChronoField.YEAR, 2023)
        .appendPattern("d MMM")
        .toFormatter(Locale.ENGLISH);
    private val formatterMonthSimple = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    private val formatterComplex = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a 'GMT'Z")
    private val formatters = listOf(formatterDaySimple, formatterDayLess, formatterMonthSimple, formatterComplex)

    fun parseDate(releaseDate: String?): Date? {
        if (releaseDate == null) return null
        formatters.forEach {
            val test = parseWithFormat(releaseDate, it)
            if (test != null) return test
        }
        return null
    }

    private fun parseWithFormat(releaseDate: String, formatter: DateTimeFormatter): Date? {
        return try {
            val localDate = LocalDate.parse(releaseDate, formatter)
            Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        } catch (e: Exception) {
            when (e) {
                is ParseException,
                is DateTimeParseException -> {
                    logger.info("Could not parse with $formatter")
                    null
                }

                else -> {
                    logger.error("There has been an unknown error: ${e.message}")
                    null
                }
            }
        }
    }
}
