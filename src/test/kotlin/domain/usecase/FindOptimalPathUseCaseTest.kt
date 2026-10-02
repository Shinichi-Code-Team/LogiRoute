package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.algorithm.routing.DijkstraRouter
import com.example.logiroute.domain.model.Warehouse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindOptimalPathUseCaseTest {

    private val dijkstraRouter: DijkstraRouter = mockk()
    private val useCase = FindOptimalPathUseCase(dijkstraRouter)

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    @Test
    fun `should find optimal path using dijkstra router`() = runTest {
        // Given
        val expectedPath = listOf(warehouseA, warehouseB)
        coEvery { dijkstraRouter.findRoute(warehouseA, warehouseB) } returns expectedPath

        // When
        val path = useCase(source = warehouseA, destination = warehouseB)

        // Then
        assertEquals(expectedPath, path)
    }
}
