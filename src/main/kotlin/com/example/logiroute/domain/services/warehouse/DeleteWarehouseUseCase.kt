package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(
        id: String
    ) {
        warehouseRepository.deleteWarehouse(id)
    }
}