package no.nav.sokos.oppdragsinfo.config

import mu.KotlinLogging

private const val SECURE_LOGGER = "secureLogger"
private const val AUDIT_LOGGER = "auditLogger"

val logger = KotlinLogging.logger {}
val secureLogger = KotlinLogging.logger(SECURE_LOGGER)
val auditLogger = KotlinLogging.logger(AUDIT_LOGGER)