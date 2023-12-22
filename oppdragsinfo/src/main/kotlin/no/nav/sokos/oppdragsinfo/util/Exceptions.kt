package no.nav.sokos.oppdragsinfo.util

import io.ktor.client.statement.HttpResponse
import no.nav.sokos.oppdragsinfo.config.ApiError

class EregException(val apiError: ApiError, val response: HttpResponse) : Exception(apiError.error)

class TpException(val apiError: ApiError, val response: HttpResponse) : Exception(apiError.error)

class PdlException(val apiError: ApiError, val response: HttpResponse) : Exception(apiError.error)
