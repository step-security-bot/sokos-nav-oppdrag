package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Grad
import no.nav.sokos.oppdragsinfo.domain.Kid
import no.nav.sokos.oppdragsinfo.domain.Kravhaver
import no.nav.sokos.oppdragsinfo.domain.Linjeenhet
import no.nav.sokos.oppdragsinfo.domain.Maksdato
import no.nav.sokos.oppdragsinfo.domain.OppdragsTekst
import no.nav.sokos.oppdragsinfo.domain.Skyldner
import no.nav.sokos.oppdragsinfo.domain.Valuta

@Serializable
data class OppdragslinjeVO(
    val oppdragsId: Int,
    val linjeId: Int,
    val delytelseId: String,
    val sats: Double,
    val typeSats: String,
    val vedtakFom: String?,
    val vedtakTom: String?,
    val attestert: String,
    val vedtaksId: String,
    val utbetalesTilId: String,
    val refunderesOrgnr: String?,
    val brukerid: String,
    val tidspktReg: String,
    val skyldnere: List<Skyldner>?,
    val valutaer: List<Valuta>?,
    val linjeenheter: List<Linjeenhet>?,
    val kidliste: List<Kid>?,
    val tekster: List<OppdragsTekst>?,
    val grader: List<Grad>?,
    val kravhavere: List<Kravhaver>?,
    val maksdatoer: List<Maksdato>?
)