package no.nav.sokos.app.config

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.io.File

object PropertiesConfig {

    private val defaultProperties = ConfigurationMap(
        mapOf(
            "NAIS_APP_NAME" to "sokos-nav-oppdrag",
            "NAIS_NAMESPACE" to "okonomi",
        )
    )
    private val localDevProperties = ConfigurationMap(
        mapOf(
            "USE_AUTHENTICATION" to "true",
            "APPLICATION_PROFILE" to Profile.LOCAL.toString(),
            "AZURE_APP_WELL_KNOWN_URL" to "https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/v2.0/.well-known/openid-configuration"
        )
    )
    private val devProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.PROD.toString()))

    private val config = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding defaultProperties
        "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding defaultProperties
        else ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(
                File("defaults.properties")
            ) overriding localDevProperties overriding defaultProperties
    }

    private operator fun get(key: String): String = config[Key(key, stringType)]

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(this["APPLICATION_PROFILE"]),
        val useAuthentication: Boolean = get("USE_AUTHENTICATION").toBoolean(),
        val azureAdConfig: AzureAdConfig = AzureAdConfig(),
    )

    data class AzureAdConfig(
        val clientId: String = this["AZURE_APP_CLIENT_ID"],
        val wellKnownUrl: String = this["AZURE_APP_WELL_KNOWN_URL"]
    )

    enum class Profile {
        LOCAL, DEV, PROD
    }

}

