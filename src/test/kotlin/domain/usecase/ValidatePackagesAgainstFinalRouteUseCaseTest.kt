package domain.usecase

import com.example.logiroute.com.example.logiroute.domain.usecase.ValidatePackagesAgainstFinalRouteUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidatePackagesAgainstFinalRouteUseCaseTest {

    private val useCase = ValidatePackagesAgainstFinalRouteUseCase()

    @Test
    fun `keeps packages whose destinations are on the final route`() {
        val origin = warehouse("WH-123")
        val destinationOnRoute = warehouse("WH-456")
        val destinationOutsideRoute = warehouse("WH-789")

        val mainPackage = packageItem(
            id = "PKG-123456",
            origin = origin,
            destination = destinationOnRoute
        )
        val otherValidPackage = packageItem(
            id = "PKG-234567",
            origin = origin,
            destination = destinationOnRoute
        )
        val packageToRemove = packageItem(
            id = "PKG-345678",
            origin = origin,
            destination = destinationOutsideRoute
        )
        val result = useCase(
            packages = listOf(mainPackage, otherValidPackage, packageToRemove),
            finalRoutePath = listOf(origin, destinationOnRoute),
            mainPackage = mainPackage
        )
        assertEquals(
            listOf(mainPackage, otherValidPackage),
            result.validPackages
        )
        assertEquals(listOf(packageToRemove), result.removedPackages)
    }
    @Test
    fun `throws when main package destination is not on the final route`() {
        val origin = warehouse("WH-123")
        val destinationOnRoute = warehouse("WH-456")
        val mainPackage = packageItem(
            id = "PKG-123456",
            origin = origin,
            destination = warehouse("WH-789")
        )
        assertFailsWith<IllegalStateException> {
            useCase(
                packages = listOf(mainPackage),
                finalRoutePath = listOf(origin, destinationOnRoute),
                mainPackage = mainPackage
            )
        }
    }
    private fun packageItem(
        id: String,
        origin: Warehouse,
        destination: Warehouse
    ) = Package(
        id = id,
        weight = 10.0,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}