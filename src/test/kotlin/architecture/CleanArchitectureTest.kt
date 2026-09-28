package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
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
}