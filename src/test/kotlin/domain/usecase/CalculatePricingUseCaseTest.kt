package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.pricingPackage.basepricing.RoutePricingEngine
import com.example.logiroute.domain.pricingPackage.servicepricing.PackageComponent
import com.example.logiroute.domain.usecase.CalculatePricingUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CalculatePricingUseCaseTest {

    private val pricingEngine = mockk<RoutePricingEngine>()
    private val useCase = CalculatePricingUseCase(pricingEngine)

    @Test
    fun `calculates package price using pricing engine and component`() {
        val packageItem = packageItem()
        val component = mockk<PackageComponent>()

        every {
            pricingEngine.computeFinalCost(
                distanceKm = 12.0,
                weight = 25.0,
                priority = Priority.STANDARD
            )
        } returns 40.0
        every { component.calculateCost(40.0) } returns 48.0
        val result = useCase(
            packageItem = packageItem,
            distanceKm = 12.0,
            packageComponent = component
        )
        assertEquals(48.0, result)
        verify(exactly = 1) {
            pricingEngine.computeFinalCost(12.0, 25.0, Priority.STANDARD)
        }
        verify(exactly = 1) { component.calculateCost(40.0) }
    }

    @Test
    fun `uses package itself as default pricing component`() {
        val packageItem = packageItem()

        every {
            pricingEngine.computeFinalCost(
                distanceKm = 12.0,
                weight = 25.0,
                priority = Priority.STANDARD
            )
        } returns 40.0
        val result = useCase(
            packageItem = packageItem,
            distanceKm = 12.0
        )
        assertEquals(40.0, result)
    }

    private fun packageItem(): Package {
        val origin = warehouse("WH-123")

        return Package(
            id = "PKG-123456",
            weight = 25.0,
            origin = origin,
            destination = warehouse("WH-456"),
            priority = Priority.STANDARD
        )
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}