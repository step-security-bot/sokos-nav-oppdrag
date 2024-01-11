package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsLinjeDetaljer(
    val harValutaer: Boolean,
    val harSkyldnere: Boolean,
    val harKravhavere: Boolean,
    val harEnheter: Boolean,
    val harGrader: Boolean,
    val harTekster: Boolean,
    val harKidliste: Boolean,
    val harMaksdatoer: Boolean
)