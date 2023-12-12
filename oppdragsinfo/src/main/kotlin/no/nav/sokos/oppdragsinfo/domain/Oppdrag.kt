package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable
@Serializable
data class Oppdrag(
    val oppdragsId: Int,
    val fagsystemId: String,
    val kodeFagOmrade: String,
    val frekvens: String,
    val kjorIdag: String,
    val stonadId: String,
    val datoForfall: String?,
    val oppdragGjelderId: String,
    val typeBilag: String,
    val brukerId: String,
    val tidspunktReg: String
//    val oppdragStatusList: List<OppdragStatus>
//    val oppdragslinjer: List<Oppdragslinje>
//    val enhetsList: List<Oppdragsenhet>,
//    val tekstList: List<OppdragsTekst>, // benyttes i visning av oppdragsdetaljer
//    val belopGrenseListe: List<Belopsgrense>, // benyttes i visning av oppdragsdetaljer
//    val omposteringList: List<Ompostering>
)