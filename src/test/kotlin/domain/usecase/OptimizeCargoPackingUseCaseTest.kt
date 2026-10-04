package domain.usecase

import com.example.logiroute.domain.usecase.OptimizeCargoPackingUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OptimizeCargoPackingUseCaseTest {
    private val origin = Warehouse("WH-001", "Origin Warehouse", "North", 32.0, 35.0)
    private val destination = Warehouse("WH-002", "Destination Warehouse", "Central", 31.9, 35.2)

    @Test
    fun `optimizer selects combination with maximum total priority within capacity`() {
        val packages = listOf(
            createPackage("PKG-000001", 4.0, Priority.LOW),
            createPackage("PKG-000002", 3.0, Priority.STANDARD),
            createPackage("PKG-000003", 5.0, Priority.URGENT),
            createPackage("PKG-000004", 2.0, Priority.STANDARD),
            createPackage("PKG-000005", 6.0, Priority.URGENT)
        )
        val maxCapacityKg = 10.0
        val optimizer = OptimizeCargoPackingUseCase()
        val selectedPackages = optimizer(packages, maxCapacityKg)
        val totalWeight = selectedPackages.sumOf {
            it.weight
        }
        assertTrue(totalWeight <= maxCapacityKg)
        assertEquals(
            setOf(
                "PKG-000002", "PKG-000003", "PKG-000004"
            ), selectedPackages.map { it.id }.toSet()
        )

    }

    private fun createPackage(id: String, weight: Double, priority: Priority): Package {
        return Package(id = id, weight = weight, origin = origin, destination = destination, priority = priority)
    }
}