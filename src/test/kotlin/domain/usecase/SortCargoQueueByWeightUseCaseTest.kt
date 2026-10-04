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
    fun `should sort packages from heaviest to lightest`() {
        // Given
        val lightPackage = packageItem("PKG-000001", 2.0)
        val heavyPackage = packageItem("PKG-000002", 10.0)
        val mediumPackage = packageItem("PKG-000003", 5.0)
        val packages = listOf(lightPackage, heavyPackage, mediumPackage)

        // When
        val result = useCase(packages)

        // Then
        assertEquals(
            listOf(heavyPackage, mediumPackage, lightPackage),
            result
        )
    }

    @Test
    fun `should preserve input order for packages with equal weights`() {
        // Given
        val firstPackage = packageItem("PKG-000001", 5.0)
        val secondPackage = packageItem("PKG-000002", 5.0)
        val packages = listOf(firstPackage, secondPackage)

        // When
        val result = useCase(packages)

        // Then
        assertEquals(listOf(firstPackage, secondPackage), result)
    }

    @Test
    fun `should leave the original package list unchanged`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 2.0),
            packageItem("PKG-000002", 10.0),
            packageItem("PKG-000003", 5.0)
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
        val packageItem = packageItem("PKG-000001", 5.0)

        // When
        val result = useCase(listOf(packageItem))

        // Then
        assertEquals(listOf(packageItem), result)
    }

    private fun packageItem(id: String, weight: Double) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}