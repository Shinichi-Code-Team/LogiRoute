package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.pricingPackage.basepricing.RoutePricingEngine
import com.example.logiroute.domain.pricingPackage.servicepricing.PackageComponent
import com.example.logiroute.domain.usecase.CalculatePricingUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CalculatePricingUseCaseTest {

    private val pricingEngine = mockk<RoutePricingEngine>()
    private val useCase = CalculatePricingUseCase(pricingEngine)

    private val packageItem = Package(
        id = "PKG-123456",
        weight = 25.0,
        origin = warehouse("WH-123"),
        destination = warehouse("WH-456"),
        priority = Priority.STANDARD
    )

    @Test
    fun `should return the price calculated by the package component`() {
        // Given
        val component = mockk<PackageComponent>()

        every {
            pricingEngine.computeFinalCost(
                distanceKm = 12.0,
                weight = 25.0,
                priority = Priority.STANDARD
            )
        } returns 40.0

        every { component.calculateCost(40.0) } returns 48.0

        // When
        val result = useCase(
            packageItem = packageItem,
            distanceKm = 12.0,
            packageComponent = component
        )

        // Then
        assertEquals(48.0, result)
    }

    @Test
    fun `should return the route price when no package component is supplied`() {
        // Given
        every {
            pricingEngine.computeFinalCost(
                distanceKm = 12.0,
                weight = 25.0,
                priority = Priority.STANDARD
            )
        } returns 40.0

        // When
        val result = useCase(
            packageItem = packageItem,
            distanceKm = 12.0
        )

        // Then
        assertEquals(40.0, result)
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}