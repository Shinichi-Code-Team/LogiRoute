package com.example.logiroute.domain.usecase.crud.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.util.suspendRunCatching

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(
        warehouse: Warehouse
    ): Result<Warehouse> =
        suspendRunCatching {
            warehouseRepository.createWarehouse(warehouse)
        }
}