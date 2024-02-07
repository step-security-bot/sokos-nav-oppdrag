package no.nav.sokos.app.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Counter

private const val METRICS_NAMESPACE = "sokos_nav_oppdrag"

object Metrics {

    val prometheusMeterRegistryApp = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val appStateRunningFalse: Counter = Counter.build()
        .namespace(METRICS_NAMESPACE)
        .name("app_state_running_false")
        .help("app state running changed to false")
        .register(prometheusMeterRegistryApp.prometheusRegistry)

    val appStateReadyFalse: Counter = Counter.build()
        .namespace(METRICS_NAMESPACE)
        .name("app_state_ready_false")
        .help("app state ready changed to false")
        .register(prometheusMeterRegistryApp.prometheusRegistry)

}