package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DispatchVehicleUseCaseTest {

    private val useCase = DispatchVehicleUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val package1 = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
    private val package2 = Package("PKG-000002", 30.0, warehouseA, warehouseB, Priority.LOW)
    private val vehicle = Vehicle("TRK-0001", 1000.0, 2.5, warehouseA)

    @Test
    fun `should dispatch vehicle successfully and update cargo queue`() = runTest {
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
        val dispatchedPackages = useCase(warehouseA, assignment)

        // Then
        assertEquals(2, dispatchedPackages.size)
        assertEquals(2, vehicle.loadedPackages.size)
        assertTrue(vehicle.loadedPackages.contains(package1))
        assertTrue(vehicle.loadedPackages.contains(package2))

        assertFalse(warehouseA.cargoQueue.contains(package1))
        assertFalse(warehouseA.cargoQueue.contains(package2))
    }
}
