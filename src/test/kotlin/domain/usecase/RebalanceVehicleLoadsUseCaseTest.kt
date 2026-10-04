package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import com.example.logiroute.domain.usecase.RebalanceVehicleLoadsUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RebalanceVehicleLoadsUseCaseTest {

    private val useCase = RebalanceVehicleLoadsUseCase()

    private val warehouseA =
        Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)

    private val warehouseB =
        Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val lowUtilizationVehicle =
        Vehicle("TRK-0001", 100.0, 2.0, warehouseA)

    private val targetVehicle =
        Vehicle("TRK-0002", 200.0, 2.5, warehouseA)

    @Test
    fun `should move packages from a low utilization vehicle to available capacity`() {
        // Given
        val packageToMove =
            Package("PKG-000001", 20.0, warehouseA, warehouseB, Priority.STANDARD)

        val packageAlreadyAssigned =
            Package("PKG-000002", 100.0, warehouseA, warehouseB, Priority.STANDARD)

        val assignments = listOf(
            VehicleAssignment(
                vehicle = lowUtilizationVehicle,
                packages = listOf(packageToMove),
                totalWeightKg = 20.0,
                remainingCapacityKg = 80.0
            ),
            VehicleAssignment(
                vehicle = targetVehicle,
                packages = listOf(packageAlreadyAssigned),
                totalWeightKg = 100.0,
                remainingCapacityKg = 100.0
            )
        )

        // When
        val result = useCase(assignments)

        // Then
        assertEquals(
            listOf(
                VehicleAssignment(
                    vehicle = targetVehicle,
                    packages = listOf(packageAlreadyAssigned, packageToMove),
                    totalWeightKg = 120.0,
                    remainingCapacityKg = 80.0
                )
            ),
            result
        )
    }

    @Test
    fun `should preserve assignments when no vehicle is below the utilization threshold`() {
        // Given
        val assignments = listOf(
            VehicleAssignment(
                vehicle = lowUtilizationVehicle,
                packages = listOf(
                    Package(
                        "PKG-000001",
                        60.0,
                        warehouseA,
                        warehouseB,
                        Priority.STANDARD
                    )
                ),
                totalWeightKg = 60.0,
                remainingCapacityKg = 40.0
            ),
            VehicleAssignment(
                vehicle = targetVehicle,
                packages = listOf(
                    Package(
                        "PKG-000002",
                        120.0,
                        warehouseA,
                        warehouseB,
                        Priority.STANDARD
                    )
                ),
                totalWeightKg = 120.0,
                remainingCapacityKg = 80.0
            )
        )

        // When
        val result = useCase(assignments)

        // Then
        assertEquals(assignments, result)
    }
}