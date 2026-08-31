    package com.example.logiroute.domain.usecase

    import com.example.logiroute.domain.logic.algorithm.routing.DijkstraRouter
    import com.example.logiroute.domain.model.Warehouse

    class FindOptimalPathUseCase(
        private val dijkstraRouter: DijkstraRouter
    ) {

        operator fun invoke(
            source: Warehouse,
            destination: Warehouse
        ): List<Warehouse> {
            return dijkstraRouter.findRoute(source, destination)
        }
    }