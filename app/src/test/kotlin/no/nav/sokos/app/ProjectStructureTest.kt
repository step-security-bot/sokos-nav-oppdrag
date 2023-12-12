package no.nav.sokos.app

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withAllAnnotationsOf
import com.lemonappdev.konsist.api.ext.list.withNameContaining
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withNameStartingWith
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.Serializable

//https://docs.konsist.lemonappdev.com/getting-started/readme

internal class ProjectStructureTest : FunSpec({

    val projectScope = Konsist.scopeFromProduction()
    val moduleNames = projectScope.files.filter {
        it.moduleName != "app"
                && it.moduleName != "venteregister" //kommenter ut når venteregister er implementert
    }.map { it.moduleName }.toSet()


    test("Moduler skal ikke importere fra andre moduler") {
        moduleNames.forEach {
            val moduleScope = Konsist.scopeFromModule(it)
            moduleScope.imports
                .withNameStartingWith("no.nav.sokos")
                .assertTrue { import ->
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

        projectScope
            .imports
            .assertFalse { it.isWildcard }

        /*   projectScope
                   .functions()
                   .assertTrue { it.numParameters < 5 }*/

    }
})