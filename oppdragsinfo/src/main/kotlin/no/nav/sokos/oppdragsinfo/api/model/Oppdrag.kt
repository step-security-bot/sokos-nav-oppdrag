package no.nav.sokos.oppdragsinfo.api.model

import java.util.*

data class Oppdrag(
    val oppdragsId: Int?,
    val gjelderNavn: String?,
    val fagsystemId: String?,
//    val fagomraade: Fagomraade?,
    val frekvens: String?,
    val kjorIdag: String?,
    val stonadId: String?,
    val datoForfall: Calendar?,
    val gjelderId: String,
    val typeBilag: String?,
    val brukerid: String?,
    val tidspktReg: String?,
//    val oppdragslinjer: List<Oppdragslinje>?,
//    val oppdragStatusList: List<OppdragStatus>?,
//    val enhetsList: List<Oppdragsenhet>?,
//    val belopGrenseListe: List<Belopsgrense>?,
//    val tekstList: List<OppdragsTekst>?,
//    val omposteringList: List<Ompostering>?,
)