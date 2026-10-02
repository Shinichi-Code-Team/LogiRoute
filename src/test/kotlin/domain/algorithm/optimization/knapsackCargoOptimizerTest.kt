package domain.algorithm.optimization

import com.example.logiroute.com.example.logiroute.domain.algorithm.optimization.knapsackCargoOptimizer
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class knapsackCargoOptimizerTest {
    private val origin = Warehouse("W1", "Origin Warehouse", "North", 32.0, 35.0)
    private val destination = Warehouse("W2", "Destination Warehouse", "Central", 31.9, 35.2)

    @Test
    fun `optimizer selects combination with maximum total priority within capacity`() {
        //Given
        val packages = listOf(
            createPackage("PKG-001", 4.0, Priority.LOW),
            createPackage("PKG-002", 3.0, Priority.STANDARD),
            createPackage("PKG-003", 5.0, Priority.URGENT),
            createPackage("PKG-004", 2.0, Priority.STANDARD),
            createPackage("PKG-005", 6.0, Priority.URGENT)
        )
        val maxCapacityKg = 10
        val optimizer = knapsackCargoOptimizer()

        //When
        val selectedPackages = optimizer.optimize(packages, maxCapacityKg)

        //Then
        val totalWeight = selectedPackages.sumOf {
            it.weight
        }
        assertTrue(totalWeight <= maxCapacityKg)
        assertEquals(
            setOf(
                "PKG-002", "PKG-003", "PKG-004"
            ), selectedPackages.map { it.id }.toSet()
        )

    }

    private fun createPackage(id: String, weight: Double, priority: Priority): Package {
        return Package(id = id, weight = weight, origin = origin, destination = destination, priority = priority)
    }
}
