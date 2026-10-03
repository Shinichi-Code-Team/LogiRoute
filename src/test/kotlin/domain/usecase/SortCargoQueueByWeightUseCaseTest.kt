package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.SortCargoQueueByWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SortCargoQueueByWeightUseCaseTest {

    private val useCase = SortCargoQueueByWeightUseCase()
    private val origin = warehouse("WH-001")
    private val destination = warehouse("WH-002")

    @Test
    fun `sorts packages from heaviest to lightest`() {
        val packages = listOf(
            packageItem("PKG-000001", 2.0),
            packageItem("PKG-000002", 10.0),
            packageItem("PKG-000003", 5.0)
        )
        val result = useCase(packages)
        assertEquals(
            listOf("PKG-000002", "PKG-000003", "PKG-000001"),
            result.map { it.id }
        )
        assertEquals(
            listOf("PKG-000001", "PKG-000002", "PKG-000003"),
            packages.map { it.id }
        )
    }

    @Test
    fun `empty input returns empty list`() {
        assertEquals(emptyList(), useCase(emptyList()))
    }

    private fun packageItem(id: String, weight: Double) =
        Package(id, weight, origin, destination, Priority.STANDARD)

    private fun warehouse(id: String) =
        Warehouse(id, "Test Warehouse", "WEST", 31.5, 34.5)
}
