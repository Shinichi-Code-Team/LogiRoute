package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.ExecuteEmergencyCargoPrioritizationRequest
import com.example.logiroute.domain.model.request.RescueOpportunity
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.usecase.ExecuteEmergencyCargoPrioritizationUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteEmergencyCargoPrioritizationUseCaseTest {

    private val packageRepository: PackageRepository = mockk()
    private val useCase =
        ExecuteEmergencyCargoPrioritizationUseCase(packageRepository)

    private val warehouseA =
        Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)

    private val warehouseB =
        Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    @Test
    fun `should load urgent package without offloading when vehicle has sufficient capacity`() =
        runTest {
            // Given
            val urgentPackage = Package(
                "PKG-000001",
                20.0,
                warehouseA,
                warehouseB,
                Priority.URGENT
            )

            val lowPriorityPackage = Package(
                "PKG-000002",
                30.0,
                warehouseA,
                warehouseB,
                Priority.LOW
            )

            val vehicle = Vehicle("TRK-0001", 100.0, 2.5, warehouseA)

            val opportunity = RescueOpportunity(
                urgentPackage = urgentPackage,
                currentWarehouse = warehouseA,
                nextHopWarehouse = warehouseB,
                availableVehicle = vehicle
            )

            val request =
                ExecuteEmergencyCargoPrioritizationRequest(opportunity)

            coEvery {
                packageRepository.getAllPackages()
            } returns listOf(lowPriorityPackage)

            // When
            val plan = useCase(request)

            // Then
            assertEquals(vehicle, plan.vehicle)
            assertTrue(plan.offloadedLowPriorityPackages.isEmpty())
            assertEquals(50.0, plan.totalWeight)
            assertEquals(50.0, plan.remainingCapacity)
        }

    @Test
    fun `should offload low priority packages when vehicle capacity is exceeded by new urgent package`() =
        runTest {
            // Given
            val urgentPackage = Package(
                "PKG-000001",
                80.0,
                warehouseA,
                warehouseB,
                Priority.URGENT
            )

            val lowPriorityPackage1 = Package(
                "PKG-000002",
                30.0,
                warehouseA,
                warehouseB,
                Priority.LOW
            )

            val lowPriorityPackage2 = Package(
                "PKG-000003",
                40.0,
                warehouseA,
                warehouseB,
                Priority.LOW
            )

            val vehicle = Vehicle("TRK-0001", 100.0, 2.5, warehouseA)

            val opportunity = RescueOpportunity(
                urgentPackage = urgentPackage,
                currentWarehouse = warehouseA,
                nextHopWarehouse = warehouseB,
                availableVehicle = vehicle
            )

            val request =
                ExecuteEmergencyCargoPrioritizationRequest(opportunity)

            coEvery {
                packageRepository.getAllPackages()
            } returns listOf(lowPriorityPackage1, lowPriorityPackage2)

            // When
            val plan = useCase(request)

            // Then
            assertEquals(vehicle, plan.vehicle)
            assertTrue(plan.offloadedLowPriorityPackages.isNotEmpty())
            assertTrue(plan.totalWeight <= vehicle.maxCapacityKg)
            assertTrue(plan.remainingCapacity >= 0.0)
        }

    @Test
    fun `should retain urgent packages already in vehicle and only offload low priority packages`() =
        runTest {
            // Given
            val newUrgentPackage = Package(
                "PKG-000004",
                50.0,
                warehouseA,
                warehouseB,
                Priority.URGENT
            )

            val existingUrgentPackage = Package(
                "PKG-000005",
                30.0,
                warehouseA,
                warehouseB,
                Priority.URGENT
            )

            val lowPriorityPackage = Package(
                "PKG-000002",
                40.0,
                warehouseA,
                warehouseB,
                Priority.LOW
            )

            val vehicle = Vehicle("TRK-0001", 100.0, 2.5, warehouseA)

            val opportunity = RescueOpportunity(
                urgentPackage = newUrgentPackage,
                currentWarehouse = warehouseA,
                nextHopWarehouse = warehouseB,
                availableVehicle = vehicle
            )

            val request =
                ExecuteEmergencyCargoPrioritizationRequest(opportunity)

            coEvery {
                packageRepository.getAllPackages()
            } returns listOf(existingUrgentPackage, lowPriorityPackage)

            // When
            val plan = useCase(request)

            // Then
            assertEquals(2, plan.loadedUrgentPackages.size)
            assertTrue(plan.loadedUrgentPackages.contains(existingUrgentPackage))
            assertTrue(plan.loadedUrgentPackages.contains(newUrgentPackage))
            assertEquals(
                listOf(lowPriorityPackage),
                plan.offloadedLowPriorityPackages
            )
            assertEquals(80.0, plan.totalWeight)
            assertEquals(20.0, plan.remainingCapacity)
        }
}