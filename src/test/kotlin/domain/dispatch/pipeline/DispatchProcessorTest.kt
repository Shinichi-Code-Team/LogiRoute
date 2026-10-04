package domain.dispatch.pipeline

import com.example.logiroute.domain.dispatch.pipeline.ExpressDispatchProcessor
import com.example.logiroute.domain.dispatch.pipeline.StandardDispatchProcessor
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.state.`package`.AssignedToVehicleState
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DispatchProcessorTest {

    private val warehouse =
        Warehouse("WH-001", "Central Hub", "WEST", 31.5, 34.5)

    private val vehicle =
        Vehicle("TRK-0001", 1000.0, 2.5, warehouse)

    @Test
    fun `standard dispatch executes full template sequence successfully`() {
        val processor = StandardDispatchProcessor()
        val packageItem = Package(
            "PKG-000001",
            50.0,
            warehouse,
            warehouse,
            Priority.STANDARD
        )

        val result = processor.dispatch(packageItem, vehicle)

        assertTrue(result.isSuccess)
        val dispatchedPackage = result.getOrNull()
        assertEquals(packageItem.id, dispatchedPackage?.id)
        assertTrue(dispatchedPackage?.state is AssignedToVehicleState)
    }

    @Test
    fun `should reject package with negative weight during creation`() {
        assertFailsWith<LogisticsException.EntityValidationException> {
            Package(
                "PKG-000002",
                -10.0,
                warehouse,
                warehouse,
                Priority.STANDARD
            )
        }
    }

    @Test
    fun `dispatch fails when vehicle capacity is exceeded`() {
        val processor = StandardDispatchProcessor()
        val heavyPackage = Package(
            "PKG-000003",
            5000.0,
            warehouse,
            warehouse,
            Priority.STANDARD
        )

        val result = processor.dispatch(heavyPackage, vehicle)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `express processor rejects non urgent priority packages`() {
        val processor = ExpressDispatchProcessor()
        val standardPackage = Package(
            "PKG-000004",
            10.0,
            warehouse,
            warehouse,
            Priority.STANDARD
        )

        val result = processor.dispatch(standardPackage, vehicle)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}