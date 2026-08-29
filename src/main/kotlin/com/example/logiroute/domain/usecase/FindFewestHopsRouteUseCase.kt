package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.logic.algorithm.routing.BfsRouter
import com.example.logiroute.domain.model.Warehouse

class FindFewestHopsRouteUseCase(
    private val bfsRouter: BfsRouter
) {
    operator fun invoke(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {
        return bfsRouter.findRoute(source, destination)
    }

}