package domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.RouteEvaluationResult
import com.example.logiroute.domain.usecase.EstimateDispatchCostUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EstimateDispatchCostUseCaseTest {

    private val useCase = EstimateDispatchCostUseCase()

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val vehicle = Vehicle("TRK-0001", 1000.0, 2.5, warehouseA)

    @Test
    fun `should calculate dispatch cost based on route total distance and vehicle cost per km`() {
        // Given
        val routeEvaluation = RouteEvaluationResult(
            totalDistanceKm = 100.0,
            totalExpectedDelayMin = 30,
            hopCount = 2
        )

        // When
        val cost = useCase(vehicle = vehicle, routeEvaluation = routeEvaluation)

        // Then
        assertEquals(250.0, cost)
    }
}
