package domain.usecase

import com.example.logiroute.domain.algorithm.routing.BfsRouter
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.FindFewestHopsRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindFewestHopsRouteUseCaseTest {

    private val bfsRouter = mockk<BfsRouter>()
    private val useCase = FindFewestHopsRouteUseCase(bfsRouter)

    @Test
    fun `passes warehouses to router and returns its route`() = runTest {
        val source = warehouse("WH-123")
        val middle = warehouse("WH-456")
        val destination = warehouse("WH-789")
        val expectedRoute = listOf(source, middle, destination)

        coEvery {
            bfsRouter.findRoute(source, destination)
        } returns expectedRoute
        val result = useCase(source, destination)
        assertEquals(expectedRoute, result)
        coVerify(exactly = 1) {
            bfsRouter.findRoute(source, destination)
        }
    }

    @Test
    fun `returns empty route when router cannot find a path`() = runTest {
        val source = warehouse("WH-123")
        val destination = warehouse("WH-789")

        coEvery {
            bfsRouter.findRoute(source, destination)
        } returns emptyList()

        val result = useCase(source, destination)
        assertEquals(emptyList(), result)
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}