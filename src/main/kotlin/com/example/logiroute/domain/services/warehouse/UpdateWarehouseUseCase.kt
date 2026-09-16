package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(
        id: String,
        warehouse: Warehouse
    ): Warehouse {
        return warehouseRepository.updateWarehouse(
            id = id,
            warehouse = warehouse
        )
    }
}