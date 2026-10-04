package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest
import com.example.logiroute.domain.usecase.PrioritizeShipmentConsolidationUseCase
import com.example.logiroute.domain.usecase.SortPackagesByPriorityAndWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrioritizeShipmentConsolidationUseCaseTest {

    private val useCase = PrioritizeShipmentConsolidationUseCase(
        SortPackagesByPriorityAndWeightUseCase()
    )

    private val origin = Warehouse(
        "WH-001",
        "Origin",
        "NORTH",
        31.5,
        34.5
    )

    private val destination = Warehouse(
        "WH-002",
        "Destination",
        "EAST",
        31.6,
        34.6
    )

    @Test
    fun `should prioritize the main and compatible packages by priority and weight`() {
        // Given
        val mainPackage = packageItem(
            "PKG-000001",
            50.0,
            Priority.STANDARD
        )
        val urgentHeavierPackage = packageItem(
            "PKG-000002",
            30.0,
            Priority.URGENT
        )
        val urgentLighterPackage = packageItem(
            "PKG-000003",
            20.0,
            Priority.URGENT
        )
        val lowPriorityPackage = packageItem(
            "PKG-000004",
            10.0,
            Priority.LOW
        )

        val request = ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = listOf(
                urgentHeavierPackage,
                lowPriorityPackage,
                urgentLighterPackage
            ),
            sharedRoute = listOf(origin, destination)
        )

        // When
        val result = useCase(request)

        // Then
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

    @Test
    fun `should return the main package when there are no compatible packages`() {
        // Given
        val mainPackage = packageItem(
            "PKG-000001",
            10.0,
            Priority.STANDARD
        )
        val request = ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = emptyList(),
            sharedRoute = listOf(origin, destination)
        )

        // When
        val result = useCase(request)

        // Then
        assertEquals(listOf(mainPackage), result)
    }

    private fun packageItem(
        id: String,
        weight: Double,
        priority: Priority
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = priority
    )
}