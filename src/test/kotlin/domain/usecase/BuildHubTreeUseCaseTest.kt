package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.HubHierarchyRaw
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildHubTreeUseCaseTest {

    private val useCase = BuildHubTreeUseCase()

    private val globalWarehouse = Warehouse("WH-001", "Global Hub", "NORTH", 31.5, 34.5)
    private val regionalWarehouse = Warehouse("WH-002", "Regional Center", "CENTER", 31.6, 34.6)
    private val localWarehouse = Warehouse("WH-003", "Local Depot", "SOUTH", 31.7, 34.7)

    @Test
    fun `should build valid hub tree with correct parent child relationships`() {
        val warehouses = listOf(globalWarehouse, regionalWarehouse, localWarehouse)
        val hierarchy = listOf(
            HubHierarchyRaw("WH-001", HubType.GLOBAL_HUB, null),
            HubHierarchyRaw("WH-002", HubType.REGIONAL_CENTER, "WH-001"),
            HubHierarchyRaw("WH-003", HubType.LOCAL_DEPOT, "WH-002")
        )

        val rootNode = useCase(warehouses, hierarchy)

        assertNotNull(rootNode)
        assertEquals(globalWarehouse, rootNode.warehouse)
        assertEquals(HubType.GLOBAL_HUB, rootNode.hubType)
        assertEquals(1, rootNode.children.size)

        val regionalNode = rootNode.children.first()
        assertEquals(regionalWarehouse, regionalNode.warehouse)
        assertEquals(HubType.REGIONAL_CENTER, regionalNode.hubType)

        val localNode = regionalNode.children.first()
        assertEquals(localWarehouse, localNode.warehouse)
        assertEquals(HubType.LOCAL_DEPOT, localNode.hubType)
    }

    @Test
    fun `should throw InvalidHubHierarchyException when invalid parent child relationship occurs`() {
        val warehouses = listOf(globalWarehouse, localWarehouse)
        val invalidHierarchy = listOf(
            HubHierarchyRaw("WH-001", HubType.GLOBAL_HUB, null),
            HubHierarchyRaw("WH-003", HubType.LOCAL_DEPOT, "WH-001")
        )

        assertThrows<LogisticsException.InvalidHubHierarchyException> {
            useCase(warehouses, invalidHierarchy)
        }
    }
}
