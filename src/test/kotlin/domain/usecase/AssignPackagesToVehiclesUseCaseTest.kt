package domain.usecase

import com.example.logiroute.domain.usecase.AssignPackagesToVehiclesUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AssignPackagesToVehiclesUseCaseTest {

    private val useCase = AssignPackagesToVehiclesUseCase()

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

    private val vehicles = listOf(
        Vehicle("TRK-0001", 1000.0, 2.5, origin),
        Vehicle("TRK-0002", 1000.0, 2.5, origin),
        Vehicle("TRK-0003", 1000.0, 2.5, origin),
        Vehicle("TRK-0004", 1000.0, 2.5, origin)
    )

    @Test
    fun `should assign packages to their expected ring vehicles`() {
        // Given
        val packageForSecondVehicle = packageItem("PKG-000011")
        val packageForThirdVehicle = packageItem("PKG-000036")
        val packageForFourthVehicle = packageItem("PKG-000061")
        val packageForFirstVehicleAfterWrap = packageItem("PKG-000086")

        val packages = listOf(
            packageForSecondVehicle,
            packageForThirdVehicle,
            packageForFourthVehicle,
            packageForFirstVehicleAfterWrap
        )

        // When
        val result = useCase(packages, vehicles)

        // Then
        assertEquals(
            mapOf(
                vehicles[0] to listOf(packageForFirstVehicleAfterWrap),
                vehicles[1] to listOf(packageForSecondVehicle),
                vehicles[2] to listOf(packageForThirdVehicle),
                vehicles[3] to listOf(packageForFourthVehicle)
            ),
            result
        )
    }

    @Test
    fun `should return an empty assignment for every vehicle when no packages are provided`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = useCase(packages, vehicles)

        // Then
        assertEquals(
            vehicles.associateWith { emptyList() },
            result
        )
    }

    @Test
    fun `should return an empty map when there are no packages or vehicles`() {
        // Given
        val packages = emptyList<Package>()
        val noVehicles = emptyList<Vehicle>()

        // When
        val result = useCase(packages, noVehicles)

        // Then
        assertEquals(emptyMap(), result)
    }

    @Test
    fun `should reject packages when no vehicles are available`() {
        // Given
        val packages = listOf(packageItem("PKG-000001"))
        val noVehicles = emptyList<Vehicle>()

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(packages, noVehicles)
        }
    }

    @Test
    fun `should reject more vehicles than the assignment ring supports`() {
        // Given
        val tooManyVehicles = vehicles + Vehicle(
            "TRK-0005",
            1000.0,
            2.5,
            origin
        )

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(emptyList(), tooManyVehicles)
        }
    }

    private fun packageItem(id: String) = Package(
        id = id,
        weight = 10.0,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
}