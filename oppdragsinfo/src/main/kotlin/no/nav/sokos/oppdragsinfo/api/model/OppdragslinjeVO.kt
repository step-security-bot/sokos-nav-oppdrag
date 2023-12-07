package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.*

@Serializable
data class OppdragslinjeVO(
    val oppdragsId: Int,
    val linjeId: Int, // benyttes i visning av oppdrag
    val delytelseId: String,
    val sats: Double, // benyttes i visning av oppdrag
    val typeSats: String,
    val vedtakFom: String?, // benyttes i visning av oppdrag
    val vedtakTom: String?, // benyttes i visning av oppdrag
    val attestert: String,
    val vedtaksId: String,
    val utbetalesTilId: String,
//    val klasse: Klasse, // benyttes i visning av oppdrag
    val refunderesOrgnr: String,
    val brukerid: String,
    val tidspktReg: String,
    val skyldnere: List<Skyldner>,
    val valutaer: List<Valuta>,
    val linjeenheter: List<Linjeenhet>,
    val kidliste: List<Kid>,
    val tekster: List<OppdragsTekst>,
    val grader: List<Grad>,
    val kravhavere: List<Kravhaver>,
    val maksdatoer: List<Maksdato>
//    val korreksjoner: List<Korreksjon>, // benyttes i visning av oppdrag
//    val attestasjoner: List<Attest>, // benyttes i visning av oppdrag
//    val statuser: List<LinjeStatus> // benyttes i visning av oppdrag
)