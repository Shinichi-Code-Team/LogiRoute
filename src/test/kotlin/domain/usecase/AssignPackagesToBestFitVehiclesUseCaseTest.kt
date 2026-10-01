package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.result.VehicleUtilization
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class AssignPackagesToBestFitVehiclesUseCaseTest {

    private val calculateVehicleUtilizationUseCase: CalculateVehicleUtilizationUseCase = mockk()
    private val useCase = AssignPackagesToBestFitVehiclesUseCase(calculateVehicleUtilizationUseCase)

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val vehicle1 = Vehicle("TRK-0001", 100.0, 2.0, warehouseA)
    private val vehicle2 = Vehicle("TRK-0002", 200.0, 3.0, warehouseA)

    @Test
    fun `should assign packages to best fit vehicle based on remaining capacity`() {
        // Given
        val package1 = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
        val packages = listOf(package1)
        val vehicles = listOf(vehicle1, vehicle2)

        every { calculateVehicleUtilizationUseCase(vehicle1) } returns VehicleUtilization(
            currentLoadKg = 0.0,
            remainingCapacityKg = 100.0,
            utilizationPercentage = 0.0
        )
        every { calculateVehicleUtilizationUseCase(vehicle2) } returns VehicleUtilization(
            currentLoadKg = 0.0,
            remainingCapacityKg = 200.0,
            utilizationPercentage = 0.0
        )

        // When
        val assignments = useCase(packages, vehicles)

        // Then
        assertEquals(1, assignments.size)
        val assignment = assignments.first()
        assertEquals(vehicle1, assignment.vehicle)
        assertEquals(listOf(package1), assignment.packages)
        assertEquals(50.0, assignment.totalWeightKg)
        assertEquals(50.0, assignment.remainingCapacityKg)
    }

    @Test
    fun `should throw NoSuitableVehicleException when no vehicle has enough capacity`() {
        // Given
        val heavyPackage = Package("PKG-000001", 300.0, warehouseA, warehouseB, Priority.STANDARD)
        val packages = listOf(heavyPackage)
        val vehicles = listOf(vehicle1, vehicle2)

        every { calculateVehicleUtilizationUseCase(vehicle1) } returns VehicleUtilization(
            currentLoadKg = 0.0,
            remainingCapacityKg = 100.0,
            utilizationPercentage = 0.0
        )
        every { calculateVehicleUtilizationUseCase(vehicle2) } returns VehicleUtilization(
            currentLoadKg = 0.0,
            remainingCapacityKg = 200.0,
            utilizationPercentage = 0.0
        )

        // When & Then
        assertThrows<LogisticsException.NoSuitableVehicleException> {
            useCase(packages, vehicles)
        }
    }
}
