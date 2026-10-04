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
    fun `should sort by highest priority and then lowest weight`() {
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
    }

    @Test
    fun `should preserve input order when priority and weight are equal`() {
        // Given
        val firstPackage = packageItem(
            "PKG-000001",
            5.0,
            Priority.URGENT
        )
        val secondPackage = packageItem(
            "PKG-000002",
            5.0,
            Priority.URGENT
        )

        // When
        val result = useCase(listOf(firstPackage, secondPackage))

        // Then
        assertEquals(listOf(firstPackage, secondPackage), result)
    }

    @Test
    fun `should leave the original package list unchanged`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 1.0, Priority.LOW),
            packageItem("PKG-000002", 8.0, Priority.URGENT),
            packageItem("PKG-000003", 5.0, Priority.STANDARD)
        )

        // When
        useCase(packages)

        // Then
        assertEquals(
            listOf("PKG-000001", "PKG-000002", "PKG-000003"),
            packages.map { it.id }
        )
    }

    @Test
    fun `should return an empty list when input is empty`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = useCase(packages)

        // Then
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should return the same package when input contains one package`() {
        // Given
        val packageItem = packageItem(
            "PKG-000001",
            5.0,
            Priority.STANDARD
        )

        // When
        val result = useCase(listOf(packageItem))

        // Then
        assertEquals(listOf(packageItem), result)
    }

    private fun packageItem(
        id: String,
        weight: Double,
        priority: Priority
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = priority
    )

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}