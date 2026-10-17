package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import com.example.logiroute.domain.usecase.DispatchVehicleUseCase
import org.junit.jupiter.api.Test
import kotlin.test.*

class DispatchVehicleUseCaseTest {

    private val useCase = DispatchVehicleUseCase()

    private val warehouseA = Warehouse(
        "WH-001", "Central Hub", "NORTH", 31.5, 34.5
    )

    private val warehouseB = Warehouse(
        "WH-002", "East Hub", "EAST", 31.6, 34.6
    )

    private val package1 = Package(
        "PKG-000001", 50.0,
        warehouseA, warehouseB, Priority.STANDARD
    )

    private val package2 = Package(
        "PKG-000002", 30.0,
        warehouseA, warehouseB, Priority.LOW
    )

    private val vehicle = Vehicle(
        "TRK-0001", 1000.0, 2.5, warehouseA
    )

    @Test
    fun `should dispatch vehicle successfully and update cargo queue`() {
        // Given
        warehouseA.addPackage(package1)
        warehouseA.addPackage(package2)

        val assignment = VehicleAssignment(
            vehicle = vehicle,
            packages = listOf(package1, package2),
            totalWeightKg = 80.0,
            remainingCapacityKg = 920.0
        )

        // When
        val result = useCase(warehouseA, assignment)

        // Then
        assertEquals(listOf(package1, package2), result)
        assertEquals(listOf(package1, package2), vehicle.loadedPackages)
        assertTrue(warehouseA.cargoQueue.isEmpty())
    }

    @Test
    fun `should reject dispatch when vehicle capacity is exceeded`() {
        // Given
        val loadedPackage = Package(
            "PKG-000003", 700.0,
            warehouseA, warehouseB, Priority.STANDARD
        )

        val newPackage = Package(
            "PKG-000004", 400.0,
            warehouseA, warehouseB, Priority.STANDARD
        )

        vehicle.loadedPackages.add(loadedPackage)
        warehouseA.addPackage(newPackage)

        val assignment = VehicleAssignment(
            vehicle = vehicle,
            packages = listOf(newPackage),
            totalWeightKg = 400.0,
            remainingCapacityKg = 300.0
        )

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            useCase(warehouseA, assignment)
        }

        assertEquals(
            listOf(loadedPackage),
            vehicle.loadedPackages
        )

        assertTrue(newPackage in warehouseA.cargoQueue)
    }
}