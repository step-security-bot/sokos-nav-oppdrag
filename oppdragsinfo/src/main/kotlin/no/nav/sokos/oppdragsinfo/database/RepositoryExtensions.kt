package no.nav.sokos.oppdragsinfo.database

import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.Parameter
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.metrics.databaseFailureCounterOppdragsInfo

val logger = KotlinLogging.logger { }

object RepositoryExtensions {

    inline fun <R> Connection.useAndHandleErrors(block: (Connection) -> R): R {
        try {
            use {
                return block(this)
            }
        } catch (ex: SQLException) {
            databaseFailureCounterOppdragsInfo.labels("${ex.errorCode}", ex.sqlState).inc()
            throw ex
        }
    }

    inline fun <reified T : Any?> ResultSet.getColumn(
        columnLabel: String,
        transform: (T) -> T = { it },
    ): T {
        val columnValue = when (T::class) {
            Int::class -> getInt(columnLabel)
            Long::class -> getLong(columnLabel)
            Char::class -> getString(columnLabel)?.get(0)
            Double::class -> getDouble(columnLabel)
            String::class -> getString(columnLabel)?.trim()
            Boolean::class -> getBoolean(columnLabel)
            BigDecimal::class -> getBigDecimal(columnLabel)
            LocalDate::class -> getDate(columnLabel)?.toLocalDate()
            LocalDateTime::class -> getTimestamp(columnLabel)?.toLocalDateTime()

            else -> {
                logger.error("Kunne ikke mappe fra resultatsett til datafelt av type ${T::class.simpleName}")
                throw SQLException("Kunne ikke mappe fra resultatsett til datafelt av type ${T::class.simpleName}") // TODO Feilhåndtering
            }
        }

        if (null !is T && columnValue == null) {
            logger.error { "Påkrevet kolonne '$columnLabel' er null" }
            throw SQLException("Påkrevet kolonne '$columnLabel' er null") // TODO Feilhåndtering
        }

        return transform(columnValue as T)
    }

    fun interface Parameter {
        fun addToPreparedStatement(sp: PreparedStatement, index: Int)
    }

    fun param(value: String?) = Parameter { sp: PreparedStatement, index: Int -> sp.setString(index, value) }
    fun param(value: Int) = Parameter { sp: PreparedStatement, index: Int -> sp.setInt(index, value) }
    fun param(value: LocalDate?) =
        Parameter { sp: PreparedStatement, index: Int -> sp.setDate(index, Date.valueOf(value)) }

    fun PreparedStatement.withParameters(vararg parameters: Parameter?) = apply {
        var index = 1; parameters.forEach { it?.addToPreparedStatement(this, index++) }
    }

    fun ResultSet.toOppdrag() = toList {
        Oppdrag(
            oppdragsId = getColumn("OPPDRAGS_ID"),
            fagsystemId = getColumn("FAGSYSTEM_ID"),
            kodeFagOmrade = getColumn("KODE_FAGOMRAADE"),
            frekvens = getColumn("FREKVENS"),
            kjorIdag = getColumn("KJOR_IDAG"),
            stonadId = getColumn("STONAD_ID"),
            datoForfall = getColumn("DATO_FORFALL"),
            oppdragGjelderId = getColumn("OPPDRAG_GJELDER_ID"),
            typeBilag = getColumn("TYPE_BILAG"),
            brukerId = getColumn("BRUKERID"),
            tidspunktReg = getColumn("TIDSPKT_REG")
        )
    }

    private fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
        while (next()) {
            add(mapper())
        }
    }
}