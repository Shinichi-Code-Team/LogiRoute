package usecase

import com.example.logiroute.domain.usecase.AnalyzeTreePerformanceUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyzeTreePerformanceUseCaseTest {

    private val useCase = AnalyzeTreePerformanceUseCase()

    @Test
    fun `returns expected sample package ids and tree measurements`() {
        val result = useCase()
        assertEquals(
            listOf(
                "PKG-000001",
                "PKG-000251",
                "PKG-000501",
                "PKG-000751",
                "PKG-001000"
            ),
            result.sampleKeys
        )

        assertEquals(5, result.unbalancedSteps.size)
        assertEquals(5, result.balancedSteps.size)

        assertEquals(1, result.unbalancedSteps["PKG-000001"])
        assertEquals(1000, result.unbalancedSteps["PKG-001000"])

        assertTrue(result.balancedHeight < result.unbalancedHeight)
    }
}