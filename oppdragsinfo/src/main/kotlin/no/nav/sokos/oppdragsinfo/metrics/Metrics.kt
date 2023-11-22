package no.nav.sokos.oppdragsinfo.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Counter

private const val METRICS_NAMESPACE = "sokos_nav_oppdrag_oppdragsinfo"

val prometheusMeterRegistryOppdragsInfo = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

val databaseFailureCounterOppdragsInfo: Counter = Counter.build()
    .namespace(METRICS_NAMESPACE)
    .name("database_failure_counter")
    .labelNames("errorCode", "sqlState")
    .help("Count database errors")
    .register(prometheusMeterRegistryOppdragsInfo.prometheusRegistry)