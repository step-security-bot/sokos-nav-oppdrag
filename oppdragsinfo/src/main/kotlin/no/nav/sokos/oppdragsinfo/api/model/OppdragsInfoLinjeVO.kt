package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Attest
import no.nav.sokos.oppdragsinfo.domain.Klasse
import no.nav.sokos.oppdragsinfo.domain.Korreksjon
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus

@Serializable
data class OppdragsInfoLinjeVO(
    val oppdragsId: Int,
    val linjeId: Int,
    val sats: Double,
    val typeSats: String,
    val vedtakFom: String?,
    val vedtakTom: String?,
    val debetKredit: String,
    val klasse: Klasse,
    val linjeIdKorreksjoner: List<Korreksjon>?,
    val attestasjoner: List<Attest>?,
    val statuser: List<LinjeStatus>?
)