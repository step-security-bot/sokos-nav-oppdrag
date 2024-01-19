package no.nav.sokos.oppdragsinfo.config

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import no.nav.sokos.oppdragsinfo.api.GjelderIdRequest
import no.nav.sokos.oppdragsinfo.api.SokOppdragRequest
import no.nav.sokos.oppdragsinfo.util.validateGjelderIdInput

fun RequestValidationConfig.oppdragsInfoRequestValidationConfig() {
    validate<SokOppdragRequest> { sokOppdragRequest ->
        when {
            validateGjelderIdInput(sokOppdragRequest.gjelderId) -> ValidationResult.Invalid("gjelderId må være satt og tillatt format er 9 eller 11 siffer")
            else -> ValidationResult.Valid
        }
    }

    validate<GjelderIdRequest> { gjelderIdRequest ->
        when {
            validateGjelderIdInput(gjelderIdRequest.gjelderId) -> ValidationResult.Invalid("gjelderId må være satt og tillatt format er 9 eller 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}