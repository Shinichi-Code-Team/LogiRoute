package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrioritizeShipmentConsolidationUseCaseTest {

    private val useCase = PrioritizeShipmentConsolidationUseCase(
        SortPackagesByPriorityAndWeightUseCase()
    )

    private val warehouseA =
        Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)

    private val warehouseB =
        Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    @Test
    fun `prioritizes packages by priority then weight`() {
        // Given
        val mainPackage = Package(
            "PKG-000001", 50.0, warehouseA, warehouseB, Priority.STANDARD
        )
        val urgentHeavierPackage = Package(
            "PKG-000002", 30.0, warehouseA, warehouseB, Priority.URGENT
        )
        val urgentLighterPackage = Package(
            "PKG-000003", 20.0, warehouseA, warehouseB, Priority.URGENT
        )
        val lowPriorityPackage = Package(
            "PKG-000004", 10.0, warehouseA, warehouseB, Priority.LOW
        )

        val opportunity = ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = listOf(
                urgentHeavierPackage,
                lowPriorityPackage,
                urgentLighterPackage
            ),
            sharedRoute = listOf(warehouseA, warehouseB)
        )
        val result = useCase(opportunity)
        assertEquals(
            listOf(
                urgentLighterPackage,
                urgentHeavierPackage,
                mainPackage,
                lowPriorityPackage
            ),
            result
        )
    }
}