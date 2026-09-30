package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.AssignmentResult
import com.example.logiroute.domain.usecase.AssignPackageToCargoQueueUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AssignPackageToCargoQueueUseCaseTest {

    private val useCase = AssignPackageToCargoQueueUseCase()

    @Test
    fun `package from warehouse is added successfully`() {

        val warehouse = warehouse("WH-123")
        val packageItem = packageItem(
            id = "PKG-123456",
            origin = warehouse,
            destination = warehouse("WH-456")
        )
        val result = useCase(warehouse, packageItem)
        assertEquals(AssignmentResult.Success, result)
        assertEquals(listOf(packageItem), warehouse.cargoQueue)
    }

    @Test
    fun `already queued package returns already queued`() {
        // Given
        val warehouse = warehouse("WH-123")
        val packageItem = packageItem(
            id = "PKG-123456",
            origin = warehouse,
            destination = warehouse("WH-456")
        )
        warehouse.addPackage(packageItem)

        // When
        val result = useCase(warehouse, packageItem)

        // Then
        assertEquals(AssignmentResult.AlreadyQueued, result)
    }

    @Test
    fun `package from another warehouse returns origin mismatch`() {
        // Given
        val warehouse = warehouse("WH-123")
        val differentOrigin = warehouse("WH-789")
        val packageItem = packageItem(
            id = "PKG-123456",
            origin = differentOrigin,
            destination = warehouse("WH-456")
        )
        val result = useCase(warehouse, packageItem)
        assertEquals(AssignmentResult.OriginMismatch, result)
        assertEquals(emptyList(), warehouse.cargoQueue)
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
        origin: Warehouse,
        destination: Warehouse
    ) = Package(
        id = id,
        weight = 10.0,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
}