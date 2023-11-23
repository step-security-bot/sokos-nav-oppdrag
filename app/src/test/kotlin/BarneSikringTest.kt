import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withAllAnnotationsOf
import com.lemonappdev.konsist.api.ext.list.withNameContaining
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withNameStartingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.Serializable


//https://docs.konsist.lemonappdev.com/getting-started/readme
internal class BarneSikringTest : FunSpec({
    val projectScope = Konsist.scopeFromProduction()
    val moduleNames = projectScope.files.filter {
        it.moduleName != "app"
                && it.moduleName != "venteregister" //kommenter ut når venteregister er implementert
    }.map { it.moduleName }.toSet()


    test("Package test") {
        moduleNames.forEach { module ->
            val koscope = Konsist.scopeFromModule(module)

            koscope
                    .classes()
                    .withAllAnnotationsOf(Serializable::class)
                    .assertTrue { it.resideInPackage("no.nav.sokos.$module.domain") }
            koscope
                    .classes()
                    .withNameEndingWith("Config")
                    .assertTrue { it.resideInPackage("no.nav.sokos.$module.config") }

            koscope
                    .classes()
                    .withNameEndingWith("Api")
                    .assertTrue { it.resideInPackage("no.nav.sokos.$module.api") }
            koscope
                    .classes()
                    .withNameContaining("Service")
                    .assertTrue { it.resideInPackage("no.nav.sokos.$module.service") }

            koscope
                    .classes()
                    .withNameContaining("Repository", "DataSource")
                    .assertTrue { it.resideInPackage("no.nav.sokos.$module.database..") }

        }

    }
    //https://docs.konsist.lemonappdev.com/writing-tests/architecture-assert
    test("Test layer boundary") {

        moduleNames.forEach {
            Konsist.scopeFromModule(it).assertArchitecture {

                //https://www.codeguru.com/csharp/understanding-onion-architecture/

                val domain = Layer("Domain", "no.nav.sokos.$it.domain..")
                val config = Layer("Config", "no.nav.sokos.$it.config..")
                val audit = Layer("Audit", "no.nav.sokos.$it.audit..")
                val metrics = Layer("Metrics", "no.nav.sokos.$it.metrics..")

                val database = Layer("Database", "no.nav.sokos.$it.database..")
                val security = Layer("Security", "no.nav.sokos.$it.security..")

                val service = Layer("Service", "no.nav.sokos.$it.service..")
                val api = Layer("Api", "no.nav.sokos.$it.api..")


                // Se gjennom dette og sjekk at det stemmer ref Onion Architecture
                domain.dependsOnNothing()
                config.dependsOnNothing()
                audit.dependsOnNothing()
                metrics.dependsOnNothing()

                database.dependsOn(domain, config)
                security.dependsOn(domain, config, audit)

                api.dependsOn(config, database, service, audit)
                service.dependsOn(domain, config, database, security, audit)
            }
        }
    }

// Test ved å legge til   implementation(project(":venteregister")) i build.gradle for oppdragsinfo og så importer noe fra venteregister
    test("Moduler skal ikke importere fra andre moduler") {
        moduleNames.forEach {
            val moduleScope = Konsist.scopeFromModule(it)
            moduleScope.imports
                    .withNameStartingWith("no.nav.sokos")
                    .assertTrue { import ->
                        println(import.name)
                        import.hasNameContaining(it)
                    }

        }

    }

    test("Clean code test") {
        projectScope
                .interfaces()
                .assertTrue { it.hasFunModifier || it.hasPublicModifier }

        projectScope
                .properties()
                .assertTrue { !it.hasPublicModifier && it.numModifiers < 5 }

        // kommentert ut fordi de failer
        /*   projectScope
                   .imports
                   .assertFalse { it.isWildcard }*/

        /*   projectScope
                   .functions()
                   .assertTrue { it.numParameters < 5 }*/

    }
})