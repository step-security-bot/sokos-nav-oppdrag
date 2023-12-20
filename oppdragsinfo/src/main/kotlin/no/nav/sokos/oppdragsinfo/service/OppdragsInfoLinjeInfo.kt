package no.nav.sokos.oppdragsinfo.service

import no.nav.sokos.oppdragsinfo.domain.Attest
import no.nav.sokos.oppdragsinfo.domain.Korreksjon
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus

data class OppdragsInfoLinjeInfo (
    val korreksjoner: List<Korreksjon>,
    val attestasjoner: List<Attest>,
    val linjestatuser: List<LinjeStatus>
)