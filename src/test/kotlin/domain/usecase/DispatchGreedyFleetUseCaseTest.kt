package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.dispatch.VehicleCoverage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DispatchGreedyFleetUseCaseTest {

    private val useCase = DispatchGreedyFleetUseCase()

    @Test
    fun `should dispatch vehicles greedy to cover all target zones`() {
        val targetZones = setOf("ZONE-NORTH", "ZONE-SOUTH", "ZONE-EAST")
        val vehicle1 = VehicleCoverage("TRK-0001", setOf("ZONE-NORTH", "ZONE-SOUTH"))
        val vehicle2 = VehicleCoverage("TRK-0002", setOf("ZONE-EAST"))
        val vehicle3 = VehicleCoverage("TRK-0003", setOf("ZONE-NORTH"))
        val vehicles = listOf(vehicle1, vehicle2, vehicle3)

        val result = useCase(targetZones, vehicles)

        assertEquals(2, result.selectedVehicleIds.size)
        assertTrue(result.selectedVehicleIds.contains("TRK-0001"))
        assertTrue(result.selectedVehicleIds.contains("TRK-0002"))
        assertEquals(targetZones, result.coveredZones)
        assertTrue(result.uncoveredZones.isEmpty())
    }

    @Test
    fun `should return uncovered zones when vehicles cannot cover all target zones`() {
        val targetZones = setOf("ZONE-NORTH", "ZONE-WEST")
        val vehicle1 = VehicleCoverage("TRK-0001", setOf("ZONE-NORTH"))
        val vehicles = listOf(vehicle1)

        val result = useCase(targetZones, vehicles)

        assertEquals(1, result.selectedVehicleIds.size)
        assertEquals(setOf("ZONE-NORTH"), result.coveredZones)
        assertEquals(setOf("ZONE-WEST"), result.uncoveredZones)
    }
}
