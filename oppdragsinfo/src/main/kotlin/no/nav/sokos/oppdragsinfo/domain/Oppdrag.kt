package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Oppdrag(
    val fagsystemId: String,
    val oppdragsId: Int,
    val navnFagGruppe: String,
    val navnFagOmraade: String,
    val kjorIdag: String,
    val typeBilag: String? = null,
    val kodeStatus: String
)