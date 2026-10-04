package domain.usecase

import com.example.logiroute.domain.model.dispatch.DispatchResult
import com.example.logiroute.domain.model.dispatch.VehicleCoverage
import com.example.logiroute.domain.usecase.DispatchGreedyFleetUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class DispatchGreedyFleetUseCaseTest {
    private val useCase = DispatchGreedyFleetUseCase()
    @Test
    fun `should select vehicles that cover all target zones`() {
        // Given
        val targetZones = setOf(
            "ZONE-NORTH",
            "ZONE-SOUTH",
            "ZONE-EAST"
        )
        val vehicles = listOf(
            VehicleCoverage(
                "TRK-0001",
                setOf("ZONE-NORTH", "ZONE-SOUTH")
            ),
            VehicleCoverage("TRK-0002", setOf("ZONE-EAST")),
            VehicleCoverage("TRK-0003", setOf("ZONE-NORTH"))
        )

        // When
        val result = useCase(targetZones, vehicles)

        // Then
        assertEquals(
            DispatchResult(
                selectedVehicleIds = listOf("TRK-0001", "TRK-0002"),
                coveredZones = targetZones,
                uncoveredZones = emptySet()
            ),
            result
        )
    }

    @Test
    fun `should report zones that available vehicles cannot cover`() {
        // Given
        val targetZones = setOf("ZONE-NORTH", "ZONE-WEST")
        val vehicles = listOf(
            VehicleCoverage("TRK-0001", setOf("ZONE-NORTH"))
        )

        // When
        val result = useCase(targetZones, vehicles)

        // Then
        assertEquals(
            DispatchResult(
                selectedVehicleIds = listOf("TRK-0001"),
                coveredZones = setOf("ZONE-NORTH"),
                uncoveredZones = setOf("ZONE-WEST")
            ),
            result
        )
    }
}
