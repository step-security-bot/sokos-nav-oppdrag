package no.nav.sokos.oppdragsinfo.config

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoRequest
import no.nav.sokos.oppdragsinfo.util.validateGjelderIdInput

fun RequestValidationConfig.oppdragsInfoRequestValidationConfig() {
    validate<OppdragsInfoRequest> { oppdragSokRequest ->
        when {
            validateGjelderIdInput(oppdragSokRequest.gjelderId) -> ValidationResult.Invalid("gjelderId må være satt og tillatt format er 9 eller 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}