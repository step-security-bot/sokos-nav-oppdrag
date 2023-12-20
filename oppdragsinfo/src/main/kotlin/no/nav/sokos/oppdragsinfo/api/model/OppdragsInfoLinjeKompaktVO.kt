package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Klasse

@Serializable
data class OppdragsInfoLinjeKompaktVO(
    val linjeId: Int,
    val sats: Double,
    val typeSats: String,
    val vedtakFom: String?,
    val vedtakTom: String?,
    val debetKredit: String,
    val klasse: Klasse,
    val linjeIdKorreksjoner: Boolean,
    val attestasjoner: Boolean,
    val statuser: Boolean
)