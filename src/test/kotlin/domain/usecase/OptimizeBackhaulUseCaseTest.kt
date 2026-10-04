package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.OptimizeBackhaulUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OptimizeBackhaulUseCaseTest {

    private val useCase = OptimizeBackhaulUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val vehicle = Vehicle("TRK-0001", 100.0, 2.5, warehouseB)

    @Test
    fun `should optimize backhaul plan by selecting candidate packages within capacity`() {
        // Given
        val urgentPackage = Package("PKG-000001", 40.0, warehouseB, warehouseA, Priority.URGENT)
        val standardPackage = Package("PKG-000002", 50.0, warehouseB, warehouseA, Priority.STANDARD)
        val heavyPackage = Package("PKG-000003", 80.0, warehouseB, warehouseA, Priority.LOW)

        val candidates = listOf(urgentPackage, standardPackage, heavyPackage)
        val returnPath = listOf(warehouseB, warehouseA)

        // When
        val plan = useCase(
            vehicle = vehicle,
            candidates = candidates,
            returnPath = returnPath
        )

        // Then
        assertEquals(vehicle, plan.vehicle)
        assertEquals(returnPath, plan.returnPath)
        assertTrue(plan.selectedPackages.contains(urgentPackage))
        assertTrue(plan.selectedPackages.contains(standardPackage))
        assertEquals(2, plan.selectedPackages.size)
    }
}
