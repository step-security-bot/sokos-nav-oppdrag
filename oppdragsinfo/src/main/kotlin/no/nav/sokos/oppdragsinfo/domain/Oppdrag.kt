package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable
@Serializable
data class Oppdrag(
    val fagsystemId: String,
    val oppdragsId: Int,
    val faggruppeNavn: String,
    val fagomraadeNavn: String,
    val kjorIdag: String,
    val bilagsType: String? = null,
    val status: String
)