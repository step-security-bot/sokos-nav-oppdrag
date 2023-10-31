package no.nav.sokos.oppdragsinfo.database

//import no.nav.sokos.app.metrics.databaseFailureCounter
import java.sql.*
import java.time.LocalDate

object RepositoryExtensions {

    inline fun <R> Connection.useAndHandleErrors(block: (Connection) -> R): R {
        try {
            use {
                return block(this)
            }
        } catch (ex: SQLException) {
//            databaseFailureCounter.labels("${ex.errorCode}", ex.sqlState).inc()
            throw ex
        }
    }

    fun interface Parameter {
        fun addToPreparedStatement(sp: PreparedStatement, index: Int)
    }

    fun param(value: String?) = Parameter { sp: PreparedStatement, index: Int -> sp.setString(index, value) }
    fun param(value: Int) = Parameter { sp: PreparedStatement, index: Int -> sp.setInt(index, value) }
    fun param(value: LocalDate?) = Parameter { sp: PreparedStatement, index: Int -> sp.setDate(index, Date.valueOf(value)) }

    fun PreparedStatement.withParameters(vararg parameters: Parameter?) = apply {
        var index = 1; parameters.forEach { it?.addToPreparedStatement(this, index++) }
    }

    fun <T> ResultSet.
            toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
        while (next()) {
            add(mapper())
        }
    }
}