package domain.state.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.state.`package`.AssignedToVehicleState
import com.example.logiroute.domain.state.`package`.CreatedState
import com.example.logiroute.domain.state.`package`.DeliveredState
import com.example.logiroute.domain.state.`package`.DeliveryFailedState
import com.example.logiroute.domain.state.`package`.InTransitState
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PackageStateTest {

    @Test
    fun `new package should start in CreatedState`() {
        // Given & When
        val packageItem = createPackage()

        // Then
        assertTrue(packageItem.state is CreatedState)
    }

    @Test
    fun `assignToVehicle should transition from CreatedState to AssignedToVehicleState`() {
        // Given
        val packageItem = createPackage()

        // When
        packageItem.assignToVehicle()

        // Then
        assertTrue(packageItem.state is AssignedToVehicleState)
    }

    @Test
    fun `startTransit should transition from AssignedToVehicleState to InTransitState`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()

        // When
        packageItem.startTransit()

        // Then
        assertTrue(packageItem.state is InTransitState)
    }

    @Test
    fun `deliver should transition from InTransitState to DeliveredState`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()
        packageItem.startTransit()

        // When
        packageItem.deliver()

        // Then
        assertTrue(packageItem.state is DeliveredState)
    }

    @Test
    fun `failDelivery should transition from InTransitState to DeliveryFailedState`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()
        packageItem.startTransit()

        // When
        packageItem.failDelivery()

        // Then
        assertTrue(packageItem.state is DeliveryFailedState)
    }

    @Test
    fun `startTransit from CreatedState should throw invalid transition exception`() {
        // Given
        val packageItem = createPackage()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.startTransit()
        }
    }

    @Test
    fun `deliver from CreatedState should throw invalid transition exception`() {
        // Given
        val packageItem = createPackage()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.deliver()
        }
    }

    @Test
    fun `deliver from AssignedToVehicleState should throw invalid transition exception`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.deliver()
        }
    }

    @Test
    fun `assignToVehicle from InTransitState should throw invalid transition exception`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()
        packageItem.startTransit()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.assignToVehicle()
        }
    }

    @Test
    fun `DeliveredState should prevent further transitions`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.deliver()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.startTransit()
        }
    }

    @Test
    fun `DeliveryFailedState should prevent further transitions`() {
        // Given
        val packageItem = createPackage()
        packageItem.assignToVehicle()
        packageItem.startTransit()
        packageItem.failDelivery()

        // When & Then
        assertThrows<LogisticsException.InvalidPackageStateTransitionException> {
            packageItem.deliver()
        }
    }

    private fun createPackage(): Package {
        val origin = Warehouse(
            id = "WH-001",
            name = "Origin Warehouse",
            regionalZone = "North",
            latitude = 32.0,
            longitude = 35.0
        )

        val destination = Warehouse(
            id = "WH-002",
            name = "Destination Warehouse",
            regionalZone = "South",
            latitude = 31.0,
            longitude = 34.0
        )

        return Package(
            id = "PKG-000001",
            weight = 10.0,
            origin = origin,
            destination = destination,
            priority = Priority.STANDARD
        )
    }
}