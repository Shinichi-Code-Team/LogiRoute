package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.AssignmentResult
import com.example.logiroute.domain.usecase.AssignPackageToCargoQueueUseCase
import com.example.logiroute.domain.usecase.SortCargoQueueByWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AssignPackageToCargoQueueUseCaseTest {

    private val sortCargoQueue = SortCargoQueueByWeightUseCase()
    private val useCase = AssignPackageToCargoQueueUseCase(sortCargoQueue)

    @Test
    fun `should return success when package origin matches warehouse`() {
        // Given
        val warehouse = warehouse("WH-001")
        val packageItem = packageItem(
            id = "PKG-000001",
            weight = 10.0,
            origin = warehouse
        )
        val result = useCase(warehouse, packageItem)

        assertEquals(AssignmentResult.Success, result)
    }

    @Test
    fun `should add the package and sort the warehouse queue by descending weight`() {
        // Given
        val warehouse = warehouse("WH-001")
        val lightPackage = packageItem("PKG-000001", 5.0, warehouse)
        val heavyPackage = packageItem("PKG-000002", 20.0, warehouse)
        val newPackage = packageItem("PKG-000003", 10.0, warehouse)

        warehouse.addPackage(lightPackage)
        warehouse.addPackage(heavyPackage)

        useCase(warehouse, newPackage)

        assertEquals(
            listOf(heavyPackage, newPackage, lightPackage),
            warehouse.cargoQueue
        )
    }

    @Test
    fun `should return already queued when package id is already in the queue`() {
        // Given
        val warehouse = warehouse("WH-001")
        val queuedPackage = packageItem("PKG-000001", 10.0, warehouse)
        val sameIdPackage = packageItem("PKG-000001", 15.0, warehouse)

        warehouse.addPackage(queuedPackage)

        val result = useCase(warehouse, sameIdPackage)

        assertEquals(AssignmentResult.AlreadyQueued, result)
    }

    @Test
    fun `should return origin mismatch when package belongs to another warehouse`() {
        // Given
        val warehouse = warehouse("WH-001")
        val otherWarehouse = warehouse("WH-002")
        val packageItem = packageItem(
            id = "PKG-000001",
            weight = 10.0,
            origin = otherWarehouse
        )

        val result = useCase(warehouse, packageItem)

        assertEquals(AssignmentResult.OriginMismatch, result)
    }

    @Test
    fun `should leave the existing queue unchanged when package origin does not match`() {
        val warehouse = warehouse("WH-001")
        val existingPackage = packageItem("PKG-000001", 10.0, warehouse)
        val otherWarehouse = warehouse("WH-002")
        val mismatchedPackage = packageItem(
            id = "PKG-000002",
            weight = 20.0,
            origin = otherWarehouse
        )
        warehouse.addPackage(existingPackage)

        useCase(warehouse, mismatchedPackage)
        assertEquals(listOf(existingPackage), warehouse.cargoQueue)
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )

    private fun packageItem(
        id: String,
        weight: Double,
        origin: Warehouse
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = warehouse("WH-003"),
        priority = Priority.STANDARD
    )
}