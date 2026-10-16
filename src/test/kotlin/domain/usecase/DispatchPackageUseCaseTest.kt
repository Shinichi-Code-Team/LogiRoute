package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.state.`package`.DeliveredState
import com.example.logiroute.domain.usecase.DispatchPackageUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DispatchPackageUseCaseTest {

    private val warehouse =
        Warehouse("WH-001", "Central Hub", "WEST", 31.5, 34.5)

    private val vehicle =
        Vehicle("TRK-0001", 1000.0, 2.5, warehouse)

    private val useCase = DispatchPackageUseCase()

    @Test
    fun `should return success when urgent package is dispatched`() {
        val packageItem = createPackage(Priority.URGENT)

        val result = useCase(packageItem, vehicle)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should deliver urgent package after dispatch`() {
        val packageItem = createPackage(Priority.URGENT)

        val result = useCase(packageItem, vehicle)

        assertIs<DeliveredState>(result.getOrThrow().state)
    }

    @Test
    fun `should return success when standard package is dispatched`() {
        val packageItem = createPackage(Priority.STANDARD)

        val result = useCase(packageItem, vehicle)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should deliver standard package after dispatch`() {
        val packageItem = createPackage(Priority.STANDARD)

        val result = useCase(packageItem, vehicle)

        assertIs<DeliveredState>(result.getOrThrow().state)
    }

    @Test
    fun `should fail when package weight exceeds vehicle capacity`() {
        val packageItem = createPackage(
            priority = Priority.STANDARD,
            weight = 1001.0
        )

        val result = useCase(packageItem, vehicle)

        assertIs<IllegalStateException>(result.exceptionOrNull())
    }

    private fun createPackage(
        priority: Priority,
        weight: Double = 25.0
    ) = Package(
        id = "PKG-000001",
        weight = weight,
        origin = warehouse,
        destination = warehouse,
        priority = priority
    )
}