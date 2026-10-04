package domain.usecase

import com.example.logiroute.domain.usecase.ReassignPackagesAfterBreakdownUseCase
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReassignPackagesAfterBreakdownUseCaseTest {

    private val useCase = ReassignPackagesAfterBreakdownUseCase()

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
    fun `should move broken vehicle packages to the next vehicle`() {
        // Given
        val brokenVehicle = vehicles[0]
        val nextVehicle = vehicles[1]
        val brokenPackage = packageItem("PKG-000001")
        val existingPackage = packageItem("PKG-000002")

        val currentAssignments = mapOf(
            brokenVehicle to listOf(brokenPackage),
            nextVehicle to listOf(existingPackage)
        )

        // When
        val result = useCase(
            currentAssignments = currentAssignments,
            vehicles = vehicles.take(2),
            brokenVehiclePosition = 15
        )

        // Then
        assertEquals(
            mapOf(nextVehicle to listOf(existingPackage, brokenPackage)),
            result
        )
    }

    @Test
    fun `should move packages from the last ring vehicle to the first vehicle`() {
        // Given
        val firstVehicle = vehicles.first()
        val lastVehicle = vehicles.last()
        val firstVehiclePackage = packageItem("PKG-000001")
        val brokenVehiclePackage = packageItem("PKG-000002")

        val currentAssignments = mapOf(
            firstVehicle to listOf(firstVehiclePackage),
            vehicles[1] to emptyList(),
            vehicles[2] to emptyList(),
            lastVehicle to listOf(brokenVehiclePackage)
        )

        // When
        val result = useCase(
            currentAssignments = currentAssignments,
            vehicles = vehicles,
            brokenVehiclePosition = 90
        )

        // Then
        assertEquals(
            mapOf(
                firstVehicle to listOf(firstVehiclePackage, brokenVehiclePackage),
                vehicles[1] to emptyList(),
                vehicles[2] to emptyList()
            ),
            result
        )
    }

    @Test
    fun `should keep assignments unchanged when broken position is not in the ring`() {
        // Given
        val packageItem = packageItem("PKG-000001")
        val currentAssignments = mapOf(vehicles[0] to listOf(packageItem))

        // When
        val result = useCase(
            currentAssignments = currentAssignments,
            vehicles = vehicles,
            brokenVehiclePosition = 999
        )

        // Then
        assertEquals(currentAssignments, result)
    }

    @Test
    fun `should keep assignments unchanged when the only vehicle breaks down`() {
        // Given
        val vehicle = vehicles.first()
        val packageItem = packageItem("PKG-000001")
        val currentAssignments = mapOf(vehicle to listOf(packageItem))

        // When
        val result = useCase(
            currentAssignments = currentAssignments,
            vehicles = listOf(vehicle),
            brokenVehiclePosition = 15
        )

        // Then
        assertEquals(currentAssignments, result)
    }

    @Test
    fun `should keep assignments unchanged when no vehicles are available`() {
        // Given
        val currentAssignments = mapOf(
            vehicles[0] to listOf(packageItem("PKG-000001"))
        )

        // When
        val result = useCase(
            currentAssignments = currentAssignments,
            vehicles = emptyList(),
            brokenVehiclePosition = 15
        )

        // Then
        assertEquals(currentAssignments, result)
    }

    private fun packageItem(id: String) = Package(
        id = id,
        weight = 10.0,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
}