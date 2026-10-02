package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RebalanceVehicleLoadsUseCaseTest {

    private val useCase = RebalanceVehicleLoadsUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val lowUtilVehicle = Vehicle("TRK-0001", 100.0, 2.0, warehouseA)
    private val targetVehicle = Vehicle("TRK-0002", 200.0, 2.5, warehouseA)

    @Test
    fun `should rebalance packages from low utilization vehicle to another vehicle with capacity`() {
        // Given
        val lightPackage = Package("PKG-000001", 20.0, warehouseA, warehouseB, Priority.STANDARD)
        val existingPackage = Package("PKG-000002", 100.0, warehouseA, warehouseB, Priority.STANDARD)

        val lowAssignment = VehicleAssignment(
            vehicle = lowUtilVehicle,
            packages = listOf(lightPackage),
            totalWeightKg = 20.0,
            remainingCapacityKg = 80.0
        )

        val targetAssignment = VehicleAssignment(
            vehicle = targetVehicle,
            packages = listOf(existingPackage),
            totalWeightKg = 100.0,
            remainingCapacityKg = 100.0
        )

        val assignments = listOf(lowAssignment, targetAssignment)

        // When
        val rebalanced = useCase(assignments)

        // Then
        assertEquals(1, rebalanced.size)
        val updatedTarget = rebalanced.first()
        assertEquals(targetVehicle, updatedTarget.vehicle)
        assertEquals(2, updatedTarget.packages.size)
        assertEquals(120.0, updatedTarget.totalWeightKg)
        assertEquals(80.0, updatedTarget.remainingCapacityKg)
    }

    @Test
    fun `should not modify assignments when all vehicles have utilization above threshold`() {
        // Given
        val heavyPackage1 = Package("PKG-000001", 60.0, warehouseA, warehouseB, Priority.STANDARD)
        val heavyPackage2 = Package("PKG-000002", 120.0, warehouseA, warehouseB, Priority.STANDARD)

        val assignment1 = VehicleAssignment(
            vehicle = lowUtilVehicle,
            packages = listOf(heavyPackage1),
            totalWeightKg = 60.0,
            remainingCapacityKg = 40.0
        )

        val assignment2 = VehicleAssignment(
            vehicle = targetVehicle,
            packages = listOf(heavyPackage2),
            totalWeightKg = 120.0,
            remainingCapacityKg = 80.0
        )

        val assignments = listOf(assignment1, assignment2)

        // When
        val rebalanced = useCase(assignments)

        // Then
        assertEquals(2, rebalanced.size)
    }
}
