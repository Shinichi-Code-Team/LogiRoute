package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.usecase.OptimizeCargoByWeightUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OptimizeCargoByWeightUseCaseTest {

    private val useCase = OptimizeCargoByWeightUseCase()

    private val origin = Warehouse(
        id = "WH-001",
        name = "Origin",
        regionalZone = "NORTH",
        latitude = 32.0,
        longitude = 35.0
    )

    private val destination = Warehouse(
        id = "WH-002",
        name = "Destination",
        regionalZone = "EAST",
        latitude = 31.9,
        longitude = 35.2
    )

    @Test
    fun `should select the package combination with the highest priority score`() {
        val packages = listOf(
            packageItem("PKG-000001", 4.0, Priority.LOW),
            packageItem("PKG-000002", 3.0, Priority.STANDARD),
            packageItem("PKG-000003", 5.0, Priority.URGENT),
            packageItem("PKG-000004", 2.0, Priority.STANDARD),
            packageItem("PKG-000005", 6.0, Priority.URGENT)
        )

        val result = useCase(
            packages = packages,
            maxCapacityKg = 10.0
        )

        assertEquals(
            listOf("PKG-000002", "PKG-000003", "PKG-000004"),
            result.map { it.id }
        )
    }

    @Test
    fun `should not exceed the weight capacity`() {
        val packages = listOf(
            packageItem("PKG-000001", 4.0, Priority.URGENT),
            packageItem("PKG-000002", 3.0, Priority.STANDARD),
            packageItem("PKG-000003", 2.0, Priority.LOW)
        )

        val result = useCase(
            packages = packages,
            maxCapacityKg = 5.0
        )

        assertTrue(result.sumOf { it.weight } <= 5.0)
    }

    @Test
    fun `should return an empty list when there are no packages`() {
        val result = useCase(
            packages = emptyList(),
            maxCapacityKg = 10.0
        )

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should return an empty list when weight capacity is zero`() {
        val result = useCase(
            packages = listOf(
                packageItem("PKG-000001", 1.0, Priority.URGENT)
            ),
            maxCapacityKg = 0.0
        )

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should skip a package that exceeds the weight capacity`() {
        val result = useCase(
            packages = listOf(
                packageItem("PKG-000001", 2.0, Priority.URGENT)
            ),
            maxCapacityKg = 1.0
        )

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should select a package when its weight exactly matches capacity`() {
        val result = useCase(
            packages = listOf(
                packageItem("PKG-000001", 2.0, Priority.URGENT)
            ),
            maxCapacityKg = 2.0
        )

        assertEquals(listOf("PKG-000001"), result.map { it.id })
    }

    @Test
    fun `should reject negative weight capacity`() {
        assertFailsWith<IllegalArgumentException> {
            useCase(
                packages = emptyList(),
                maxCapacityKg = -1.0
            )
        }
    }

    @Test
    fun `should reject NaN weight capacity`() {
        assertFailsWith<IllegalArgumentException> {
            useCase(
                packages = emptyList(),
                maxCapacityKg = Double.NaN
            )
        }
    }

    @Test
    fun `should reject infinite weight capacity`() {
        assertFailsWith<IllegalArgumentException> {
            useCase(
                packages = emptyList(),
                maxCapacityKg = Double.POSITIVE_INFINITY
            )
        }
    }

    @Test
    fun `should reject a package with NaN weight during creation`() {
        assertFailsWith<LogisticsException.EntityValidationException> {
            packageItem(
                id = "PKG-000006",
                weight = Double.NaN,
                priority = Priority.URGENT
            )
        }
    }

    @Test
    fun `should reject a package with infinite weight during creation`() {
        assertFailsWith<LogisticsException.EntityValidationException> {
            packageItem(
                id = "PKG-000007",
                weight = Double.POSITIVE_INFINITY,
                priority = Priority.URGENT
            )
        }
    }

    @Test
    fun `should select both packages when only weight capacity is enforced`() {
        val packages = bulkyAndDensePackages()

        val result = useCase(
            packages = packages,
            maxCapacityKg = 10.0
        )

        assertEquals(
            listOf("PKG-000101", "PKG-000102"),
            result.map { it.id }
        )
    }

    @Test
    fun `should enforce volume capacity alongside weight capacity`() {
        val packages = bulkyAndDensePackages()

        val result = useCase(
            packages = packages,
            maxCapacityKg = 10.0,
            maxVolumeM3 = 1.0
        )

        assertEquals(listOf("PKG-000101"), result.map { it.id })
    }

    @Test
    fun `should not exceed the volume capacity`() {
        val result = useCase(
            packages = bulkyAndDensePackages(),
            maxCapacityKg = 10.0,
            maxVolumeM3 = 1.0
        )

        assertTrue(result.sumOf { it.volumeM3 ?: 0.0 } <= 1.0)
    }

    @Test
    fun `should reject negative volume capacity`() {
        assertFailsWith<IllegalArgumentException> {
            useCase(
                packages = emptyList(),
                maxCapacityKg = 10.0,
                maxVolumeM3 = -1.0
            )
        }
    }

    @Test
    fun `should reject a package without volume when volume capacity is enforced`() {
        assertFailsWith<IllegalArgumentException> {
            useCase(
                packages = listOf(
                    packageItem(
                        id = "PKG-000103",
                        weight = 2.0,
                        priority = Priority.URGENT
                    )
                ),
                maxCapacityKg = 10.0,
                maxVolumeM3 = 1.0
            )
        }
    }

    private fun bulkyAndDensePackages(): List<Package> =
        listOf(
            packageItem(
                id = "PKG-000101",
                weight = 2.0,
                priority = Priority.URGENT,
                volumeM3 = 0.9
            ),
            packageItem(
                id = "PKG-000102",
                weight = 8.0,
                priority = Priority.STANDARD,
                volumeM3 = 0.2
            )
        )

    private fun packageItem(
        id: String,
        weight: Double,
        priority: Priority,
        volumeM3: Double? = null
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = priority,
        volumeM3 = volumeM3
    )
}