package com.example.logiroute.domain.tree

import com.example.logiroute.com.example.logiroute.domain.model.request.HubHierarchyRaw
import com.example.logiroute.com.example.logiroute.domain.model.request.HubNode
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.WarehouseNotFoundException


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
            ?: throw WarehouseNotFoundException(raw.warehouseId)

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
}