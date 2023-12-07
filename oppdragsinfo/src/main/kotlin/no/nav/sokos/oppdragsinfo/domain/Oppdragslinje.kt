package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Oppdragslinje(
    val linjeId: Int, // benyttes i visning av oppdrag
    val delytelseId: String,
    val sats: Double, // benyttes i visning av oppdrag
    val typeSats: Satstype, // benyttes i visning av oppdrag
    val vedtakFom: String, // benyttes i visning av oppdrag
    val vedtakTom: String?, // benyttes i visning av oppdrag
    val attestert: String,
    val vedtaksId: String,
    val utbetalesTilId: String,
    val klasse: Klasse, // benyttes i visning av oppdrag
    val refunderesOrgnr: String,
    val brukerid: String,
    val tidspktReg: String,
    val korreksjoner: List<Korreksjon>, // benyttes i visning av oppdrag
    val attestasjoner: List<Attest>, // benyttes i visning av oppdrag
    val statuser: List<LinjeStatus> // benyttes i visning av oppdrag
)