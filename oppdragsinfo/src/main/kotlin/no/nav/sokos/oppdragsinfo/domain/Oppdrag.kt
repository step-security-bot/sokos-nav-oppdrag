package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable
@Serializable
data class Oppdrag(
    val gjelderId: String? = null,
    val gjelderNavn: String? = null,
    val datoFom: String? = null,
    val fagsystemId: String,
    val oppdragsId: Int,
    val fagGruppeNavn: String,
    val fagOmraadeNavn: String,
    val bilagsType: String,
    val status: String
)