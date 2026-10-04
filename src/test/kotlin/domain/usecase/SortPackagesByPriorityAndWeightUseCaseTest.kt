package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.SortPackagesByPriorityAndWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SortPackagesByPriorityAndWeightUseCaseTest {

    private val useCase = SortPackagesByPriorityAndWeightUseCase()
    private val origin = warehouse("WH-001")
    private val destination = warehouse("WH-002")

    @Test
    fun `sorts by highest priority then lowest weight`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 1.0, Priority.LOW),
            packageItem("PKG-000002", 8.0, Priority.URGENT),
            packageItem("PKG-000003", 5.0, Priority.STANDARD),
            packageItem("PKG-000004", 3.0, Priority.URGENT),
            packageItem("PKG-000005", 2.0, Priority.STANDARD)
        )

        // When
        val result = useCase(packages)

        // Then
        assertEquals(
            listOf(
                "PKG-000004",
                "PKG-000002",
                "PKG-000005",
                "PKG-000003",
                "PKG-000001"
            ),
            result.map { it.id }
        )
        assertEquals(
            listOf(
                "PKG-000001",
                "PKG-000002",
                "PKG-000003",
                "PKG-000004",
                "PKG-000005"
            ),
            packages.map { it.id }
        )
    }

    @Test
    fun `empty input returns empty list`() {
        assertEquals(emptyList(), useCase(emptyList()))
    }

    private fun packageItem(
        id: String,
        weight: Double,
        priority: Priority
    ) = Package(id, weight, origin, destination, priority)

    private fun warehouse(id: String) =
        Warehouse(id, "Test Warehouse", "WEST", 31.5, 34.5)
}