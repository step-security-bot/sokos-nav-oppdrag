package no.nav.sokos.oppdragsinfo.service

import no.nav.sokos.oppdragsinfo.domain.Grad
import no.nav.sokos.oppdragsinfo.domain.Kid
import no.nav.sokos.oppdragsinfo.domain.Kravhaver
import no.nav.sokos.oppdragsinfo.domain.Linjeenhet
import no.nav.sokos.oppdragsinfo.domain.Maksdato
import no.nav.sokos.oppdragsinfo.domain.OppdragsTekst
import no.nav.sokos.oppdragsinfo.domain.Oppdragslinje
import no.nav.sokos.oppdragsinfo.domain.Skyldner
import no.nav.sokos.oppdragsinfo.domain.Valuta

data class OppdragsLinjeInfo (
    val oppdragslinjer: List<Oppdragslinje>,
    val skyldnere: List<Skyldner>,
    val valutaer: List<Valuta>,
    val linjeenheter: List<Linjeenhet>,
    val kidliste: List<Kid>,
    val tekster: List<OppdragsTekst>,
    val grader: List<Grad>,
    val kravhavere: List<Kravhaver>,
    val maksdatoer: List<Maksdato>
)