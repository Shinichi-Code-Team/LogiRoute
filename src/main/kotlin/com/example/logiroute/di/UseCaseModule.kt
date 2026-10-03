package com.example.logiroute.di

import AddVehicleToHubUseCase
import com.example.logiroute.domain.usecase.AssignPackagesToVehiclesUseCase
import com.example.logiroute.domain.usecase.ReassignPackagesAfterBreakdownUseCase
import com.example.logiroute.com.example.logiroute.domain.usecase.ValidatePackagesAgainstFinalRouteUseCase
import com.example.logiroute.domain.algorithm.routing.BfsRouter
import com.example.logiroute.domain.algorithm.routing.DijkstraRouter
import com.example.logiroute.domain.algorithm.routing.PathConstructor
import com.example.logiroute.domain.pricingPackage.basepricing.EcoStrategy
import com.example.logiroute.domain.pricingPackage.basepricing.RoutePricingEngine
import com.example.logiroute.domain.usecase.*
import com.example.logiroute.domain.usecase.crud.`package`.*
import com.example.logiroute.domain.usecase.crud.route.*
import com.example.logiroute.domain.usecase.crud.vehicle.*
import com.example.logiroute.domain.usecase.crud.warehouse.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single { PathConstructor() }

    single {
        DijkstraRouter(
            warehousesRepository = get(),
            pathConstructor = get(),
            routeWeight = { route -> route.distanceKm }
        )
    }

    single(named("delayRouter")) {
        DijkstraRouter(
            warehousesRepository = get(),
            pathConstructor = get(),
            routeWeight = { route -> route.typicalDelayMin.toDouble() }
        )
    }

    single {
        BfsRouter(
            warehouseRepository = get(),
            pathConstructor = get()
        )
    }

    single { RoutePricingEngine(EcoStrategy()) }
    factoryOf(::AddVehicleToHubUseCase)
    factoryOf(::AnalyzeTreePerformanceUseCase)
    factoryOf(::AssignPackagesToBestFitVehiclesUseCase)
    factoryOf(::AssignPackagesToVehiclesUseCase)
    factoryOf(::AssignPackageToCargoQueueUseCase)
    factoryOf(::CalculatePricingUseCase)
    factoryOf(::CalculateVehicleUtilizationUseCase)
    factoryOf(::DetectEmergencyCargoRescueOpportunitiesUseCase)
    factoryOf(::DetectShipmentConsolidationOpportunitiesUseCase)
    factoryOf(::DispatchVehicleUseCase)
    factoryOf(::EstimateDispatchCostUseCase)
    factoryOf(::EvaluateRouteUseCase)
    factoryOf(::ExecuteEmergencyCargoPrioritizationUseCase)
    factoryOf(::FindBackhaulCandidatesUseCase)
    factoryOf(::FindFewestHopsRouteUseCase)
    factoryOf(::FindOptimalPathUseCase)
    factoryOf(::FindStationedVehiclesByCapacityUseCase)
    factoryOf(::GetWarehouseLoadFactorUseCase)
    factoryOf(::OptimizeBackhaulUseCase)
    factoryOf(::PrioritizeShipmentConsolidationUseCase)
    factoryOf(::ReassignPackagesAfterBreakdownUseCase)
    factoryOf(::RebalanceVehicleLoadsUseCase)
    factoryOf(::RoutePackageUseCase)
    factoryOf(::TraceHubLineageUseCase)
    factoryOf(::ValidatePackagesAgainstFinalRouteUseCase)

    // CRUD use cases
    factoryOf(::CreatePackageUseCase)
    factoryOf(::DeletePackageUseCase)
    factoryOf(::ReadPackageUseCase)
    factoryOf(::UpdatePackageUseCase)
    factoryOf(::OptimizeCargoPackingUseCase)
    factoryOf(::SortPackagesByPriorityAndWeightUseCase)
    factoryOf(::SortCargoQueueByWeightUseCase)
    factoryOf(::CreateRouteUseCase)
    factoryOf(::DeleteRouteUseCase)
    factoryOf(::ReadRouteUseCase)
    factoryOf(::UpdateRouteUseCase)

    factoryOf(::CreateVehicleUseCase)
    factoryOf(::DeleteVehicleUseCase)
    factoryOf(::ReadVehicleUseCase)
    factoryOf(::UpdateVehicleUseCase)

    factoryOf(::CreateWarehouseUseCase)
    factoryOf(::DeleteWarehouseUseCase)
    factoryOf(::ReadWarehouseUseCase)
    factoryOf(::UpdateWarehouseUseCase)
    factory {
        SelectShipmentRouteUseCase(
            distanceRouter = get(),
            delayRouter = get(named("delayRouter")),
            bfsRouter = get()
        )
    }
}