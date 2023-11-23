package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.Parameter
import no.nav.sokos.oppdragsinfo.metrics.databaseFailureCounterOppdragsInfo

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

    fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
        while (next()) {
            add(mapper())
        }
    }
}