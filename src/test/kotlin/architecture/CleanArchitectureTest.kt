package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.verify.assertFalse
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
}