package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class CleanArchitectureTest {

    @Test
    fun domain_should_not_depend_on_data_layer() {
        Konsist.scopeFromProject()
            .assertArchitecture {
                val domain = Layer("Domain", "..domain..")
                val data = Layer("Data", "..data..")

                domain.dependsOnNothing()
                data.dependsOn(domain)
            }
    }

    @Test
    fun domain_should_not_import_serialization() {
        Konsist.scopeFromProject()
            .files
            .filter { it.packagee?.name?.contains("domain") == true }
            .assertFalse {
                it.imports.any { import ->
                    import.name.contains("kotlinx.serialization")
                }
            }
    }

    @Test
    fun usecases_should_end_with_usecase_suffix() {
        Konsist.scopeFromProject()
            .classes()
            .filter {
                it.packagee?.name?.contains("domain.usecase") == true
            }
            .assertTrue {
                it.name.endsWith("UseCase")
            }
    }

    @Test
    fun validators_should_end_with_validator_suffix() {
        Konsist.scopeFromProject()
            .classes()
            .filter {
                it.packagee?.name?.contains("domain.validator") == true
            }
            .assertTrue {
                it.name.endsWith("Validator")
            }
    }

    @Test
    fun usecases_should_have_invoke_operator() {
        Konsist.scopeFromProject()
            .classes()
            .filter {
                it.packagee?.name?.contains("domain.usecase") == true
            }
            .assertTrue { clazz ->

                clazz.functions().any {
                    it.name == "invoke"
                }

            }
    }

    @Test
    fun dto_classes_should_end_with_dto_suffix() {
        Konsist.scopeFromProject()
            .classes()
            .filter {
                it.packagee?.name?.contains("data.remote.dto") == true
            }
            .assertTrue {
                it.name.endsWith("Dto")
            }
    }
}