package com.example.logiroute.domain.logic.algorithm.tree

import com.example.logiroute.com.example.logiroute.domain.model.request.HubHierarchyRaw
import com.example.logiroute.com.example.logiroute.domain.model.request.HubNode
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class HubTreeBuilder {

    fun buildTree(
        warehouses: List<Warehouse>,
        hierarchy: List<HubHierarchyRaw>
    ): HubNode {

        val warehouseMap = warehouses.associateBy { it.id }

        val rootRaw = hierarchy.first {
            it.hubType == HubType.GLOBAL_HUB &&
                    it.parentWarehouseId == null
        }

        return buildNode(
            raw = rootRaw,
            warehouseMap = warehouseMap,
            hierarchy = hierarchy,
            parent = null
        )
    }

    private fun buildNode(
        raw: HubHierarchyRaw,
        warehouseMap: Map<String, Warehouse>,
        hierarchy: List<HubHierarchyRaw>,
        parent: HubNode?
    ): HubNode {

        val warehouse = warehouseMap[raw.warehouseId]
            ?: throw LogisticsException.WarehouseNotFoundException(raw.warehouseId)
        validateParentChildRelationship(
            parent = parent,
            childType = raw.hubType
        )

        val node = HubNode(
            warehouse = warehouse,
            hubType = raw.hubType,
            parentHub = parent
        )

        hierarchy
            .filter { it.parentWarehouseId == raw.warehouseId }
            .map { childRaw ->
                buildNode(
                    raw = childRaw,
                    warehouseMap = warehouseMap,
                    hierarchy = hierarchy,
                    parent = node
                )
            }
            .forEach { child ->
                node.children.add(child)
            }

        return node
    }

    private fun validateParentChildRelationship(
        parent: HubNode?,
        childType: HubType
    ) {
        if (parent == null) return

        val validRelationship = when (parent.hubType) {
            HubType.GLOBAL_HUB ->
                childType == HubType.REGIONAL_CENTER

            HubType.REGIONAL_CENTER ->
                childType == HubType.LOCAL_DEPOT

            HubType.LOCAL_DEPOT ->
                false
        }

        if (!validRelationship) {
            throw LogisticsException.InvalidHubHierarchyException(
                "Invalid hierarchy: ${parent.hubType} cannot have $childType as a child."
            )
        }
    }
}
