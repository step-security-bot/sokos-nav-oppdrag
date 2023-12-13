package no.nav.sokos.oppdragsinfo.config

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.util.validateGjelderIdInput

fun RequestValidationConfig.oppdragsInfoRequestValidationConfig() {
    validate<OppdragsSokRequest> { oppdragSokRequest ->
        when {
            validateGjelderIdInput(oppdragSokRequest.gjelderId) -> ValidationResult.Invalid("GjelderId må være satt")
            else -> ValidationResult.Valid
        }
    }
}