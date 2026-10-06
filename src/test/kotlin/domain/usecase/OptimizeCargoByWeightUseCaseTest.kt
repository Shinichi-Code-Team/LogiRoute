package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.OptimizeCargoByWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OptimizeCargoByWeightUseCaseTest {

    private val useCase = OptimizeCargoByWeightUseCase()

    private val origin = Warehouse(
        "WH-001",
        "Origin",
        "NORTH",
        32.0,
        35.0
    )

    private val destination = Warehouse(
        "WH-002",
        "Destination",
        "EAST",
        31.9,
        35.2
    )

    @Test
    fun `should select the package combination with the highest priority score`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 4.0, Priority.LOW),
            packageItem("PKG-000002", 3.0, Priority.STANDARD),
            packageItem("PKG-000003", 5.0, Priority.URGENT),
            packageItem("PKG-000004", 2.0, Priority.STANDARD),
            packageItem("PKG-000005", 6.0, Priority.URGENT)
        )

        // When
        val result = useCase(packages, maxCapacityKg = 10.0)

        // Then
        assertEquals(
            listOf("PKG-000002", "PKG-000003", "PKG-000004"),
            result.map { it.id }
        )
    }

    @Test
    fun `should not exceed the vehicle capacity`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 4.0, Priority.URGENT),
            packageItem("PKG-000002", 3.0, Priority.STANDARD),
            packageItem("PKG-000003", 2.0, Priority.LOW)
        )
        val capacity = 5.0

        // When
        val result = useCase(packages, capacity)

        // Then
        assertTrue(result.sumOf { it.weight } <= capacity)
    }

    @Test
    fun `should return an empty list when the package list is empty`() {
        // Given
        val packages = emptyList<Package>()

        // When
        val result = useCase(packages, maxCapacityKg = 10.0)

        // Then
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should return an empty list when capacity is zero`() {
        // Given
        val packages = listOf(
            packageItem("PKG-000001", 1.0, Priority.URGENT)
        )

        // When
        val result = useCase(packages, maxCapacityKg = 0.0)

        // Then
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should skip a package that weighs more than the capacity`() {
        // Given
        val packageItem = packageItem("PKG-000001", 2.0, Priority.URGENT)

        // When
        val result = useCase(listOf(packageItem), maxCapacityKg = 1.0)

        // Then
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should select a package when its weight exactly matches capacity`() {
        // Given
        val packageItem = packageItem("PKG-000001", 2.0, Priority.URGENT)

        // When
        val result = useCase(listOf(packageItem), maxCapacityKg = 2.0)

        // Then
        assertEquals(listOf(packageItem), result)
    }

    @Test
    fun `should reject negative capacity`() {
        // Given
        val packages = emptyList<Package>()

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(packages, maxCapacityKg = -1.0)
        }
    }

    @Test
    fun `should reject NaN capacity`() {
        // Given
        val packages = emptyList<Package>()

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(packages, maxCapacityKg = Double.NaN)
        }
    }

    @Test
    fun `should reject infinite capacity`() {
        // Given
        val packages = emptyList<Package>()

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(packages, maxCapacityKg = Double.POSITIVE_INFINITY)
        }
    }

    @Test
    fun `should reject a package with NaN weight`() {
        // Given
        val invalidPackage = packageItem(
            "PKG-000001",
            Double.NaN,
            Priority.URGENT
        )

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(listOf(invalidPackage), maxCapacityKg = 10.0)
        }
    }

    @Test
    fun `should reject a package with infinite weight`() {
        // Given
        val invalidPackage = packageItem(
            "PKG-000001",
            Double.POSITIVE_INFINITY,
            Priority.URGENT
        )

        // When / Then
        assertFailsWith<IllegalArgumentException> {
            useCase(listOf(invalidPackage), maxCapacityKg = 10.0)
        }
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
}