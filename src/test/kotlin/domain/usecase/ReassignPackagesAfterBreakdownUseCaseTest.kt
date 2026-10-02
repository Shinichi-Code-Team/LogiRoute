package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.usecase.ReassignPackagesAfterBreakdownUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ReassignPackagesAfterBreakdownUseCaseTest {

    private val useCase = ReassignPackagesAfterBreakdownUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val vehicle1 = Vehicle("TRK-0001", 1000.0, 2.5, warehouseA)
    private val vehicle2 = Vehicle("TRK-0002", 800.0, 2.0, warehouseA)

    @Test
    fun `should reassign packages from broken vehicle to next vehicle in ring`() {
        // Given
        val package1 = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
        val package2 = Package("PKG-000002", 30.0, warehouseA, warehouseB, Priority.URGENT)

        val vehicles = listOf(vehicle1, vehicle2)
        val currentAssignments = mapOf(
            vehicle1 to listOf(package1),
            vehicle2 to listOf(package2)
        )

        val brokenPosition = 15

        // When
        val updatedAssignments = useCase(
            currentAssignments = currentAssignments,
            vehicles = vehicles,
            brokenVehiclePosition = brokenPosition
        )

        // Then
        assertNotNull(updatedAssignments)
        assertFalse(updatedAssignments.containsKey(vehicle1))
        assertTrue(updatedAssignments.containsKey(vehicle2))
        assertEquals(2, updatedAssignments.getValue(vehicle2).size)
        assertTrue(updatedAssignments.getValue(vehicle2).contains(package1))
        assertTrue(updatedAssignments.getValue(vehicle2).contains(package2))
    }

    @Test
    fun `should return unchanged assignments when broken position does not match any vehicle`() {
        // Given
        val package1 = Package("PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD)
        val vehicles = listOf(vehicle1)
        val currentAssignments = mapOf(vehicle1 to listOf(package1))

        val invalidBrokenPosition = 999

        // When
        val updatedAssignments = useCase(
            currentAssignments = currentAssignments,
            vehicles = vehicles,
            brokenVehiclePosition = invalidBrokenPosition
        )

        // Then
        assertEquals(currentAssignments, updatedAssignments)
    }
}
