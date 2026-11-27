package com.example.logiroute.di

import com.example.logiroute.data.cache.LruRoutePathCache
import com.example.logiroute.domain.cache.RouteCostMetric
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.domain.usecase.AddVehicleToHubUseCase
import com.example.logiroute.domain.usecase.ValidatePackagesAgainstFinalRouteUseCase
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import com.example.logiroute.domain.model.command.CommandHistoryStore
import com.example.logiroute.domain.model.command.TreeCommandHistoryStore
import com.example.logiroute.domain.pricingPackage.basepricing.EcoStrategy
import com.example.logiroute.domain.pricingPackage.basepricing.RoutePricingEngine
import com.example.logiroute.domain.usecase.*
import com.example.logiroute.domain.usecase.crud.`package`.*
import com.example.logiroute.domain.usecase.crud.route.*
import com.example.logiroute.domain.usecase.crud.vehicle.*
import com.example.logiroute.domain.usecase.crud.warehouse.*
import com.example.logiroute.domain.util.crud.`package`.CreatePackageUseCase
import com.example.logiroute.domain.util.crud.`package`.DeletePackageUseCase
import com.example.logiroute.domain.util.crud.`package`.ReadPackageUseCase
import com.example.logiroute.domain.util.crud.`package`.UpdatePackageUseCase
import com.example.logiroute.domain.util.crud.route.CreateRouteUseCase
import com.example.logiroute.domain.util.crud.route.DeleteRouteUseCase
import com.example.logiroute.domain.util.crud.route.ReadRouteUseCase
import com.example.logiroute.domain.util.crud.route.UpdateRouteUseCase
import com.example.logiroute.domain.util.crud.vehicle.CreateVehicleUseCase
import com.example.logiroute.domain.util.crud.vehicle.DeleteVehicleUseCase
import com.example.logiroute.domain.util.crud.vehicle.ReadVehicleUseCase
import com.example.logiroute.domain.util.crud.vehicle.UpdateVehicleUseCase
import com.example.logiroute.domain.util.crud.warehouse.CreateWarehouseUseCase
import com.example.logiroute.domain.util.crud.warehouse.DeleteWarehouseUseCase
import com.example.logiroute.domain.util.crud.warehouse.ReadWarehouseUseCase
import com.example.logiroute.domain.util.crud.warehouse.UpdateWarehouseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single<RoutePathCache> { LruRoutePathCache(capacity = 50) }

    factory {
        FindOptimalPathUseCase(
            warehousesRepository = get(),
            routeRepository = get(),
            routeWeight = { route -> route.distanceKm },
            routePathCache = get(),
            routeCostMetric = RouteCostMetric.DISTANCE
        )
    }

    factory(named("delayRouter")) {
        FindOptimalPathUseCase(
            warehousesRepository = get(),
            routeRepository = get(),
            routeWeight = { route ->
                route.typicalDelayMin.toDouble()
            },
            routePathCache = get(),
            routeCostMetric = RouteCostMetric.DELAY
        )
    }
    factory {
        TrackActiveFleetUseCase(
            telemetryStreamingService = get()
    )
    }
    factoryOf(::FindFewestHopsRouteUseCase)
    factoryOf(::FindFewestHopsBidirectionalRouteUseCase)

    factory {
        SelectShipmentRouteUseCase(
            distancePathUseCase = get(),
            delayPathUseCase = get(named("delayRouter")),
            fewestHopsRouteUseCase = get() ,
        )
    }

    single {
        RoutePricingEngine(EcoStrategy())
    }

    single { CommandHistoryStore() }
    factoryOf(::ExecuteLogisticsCommandUseCase)
    factoryOf(::UndoLogisticsCommandUseCase)
    factoryOf(::RedoLogisticsCommandUseCase)

    single { TreeCommandHistoryStore() }
    factoryOf(::ExecuteBranchingCommandUseCase)
    factoryOf(::UndoBranchingCommandUseCase)
    factoryOf(::RedoBranchingCommandUseCase)

    factoryOf(::ClassifyPackageSectorUseCase)

    factoryOf(::AddVehicleToHubUseCase)
    factoryOf(::AnalyzeTreePerformanceUseCase)
    factoryOf(::AssignPackagesToBestFitVehiclesUseCase)
    factoryOf(::AssignPackagesToVehiclesUseCase)
    factoryOf(::AssignPackageToCargoQueueUseCase)
    factoryOf(::BuildHubTreeUseCase)
    factoryOf(::CalculatePricingUseCase)
    factoryOf(::CalculateVehicleUtilizationUseCase)
    factoryOf(::DetectEmergencyCargoRescueOpportunitiesUseCase)
    factoryOf(::DetectShipmentConsolidationOpportunitiesUseCase)
    factoryOf(::DispatchGreedyFleetUseCase)
    factoryOf(::DispatchVehicleUseCase)
    factoryOf(::EstimateDispatchCostUseCase)
    factoryOf(::EvaluateRouteUseCase)
    factoryOf(::ExecuteEmergencyCargoPrioritizationUseCase)
    factoryOf(::FindBackhaulCandidatesUseCase)
    factoryOf(::FindStationedVehiclesByCapacityUseCase)
    factoryOf(::GetWarehouseLoadFactorUseCase)
    factoryOf(::OptimizeBackhaulUseCase)
    factoryOf(::OptimizeCargoByWeightUseCase)
    factoryOf(::PrioritizeShipmentConsolidationUseCase)
    factoryOf(::ReassignPackagesAfterBreakdownUseCase)
    factoryOf(::RebalanceVehicleLoadsUseCase)
    factoryOf(::RoutePackageUseCase)
    factoryOf(::SortPackagesByPriorityAndWeightUseCase)
    factoryOf(::SortCargoQueueByWeightUseCase)
    factoryOf(::TraceHubLineageUseCase)
    factoryOf(::ValidatePackagesAgainstFinalRouteUseCase)
    factoryOf(::OptimizeLastMileRouteUseCase)

    factoryOf(::CreatePackageUseCase)
    factoryOf(::DeletePackageUseCase)
    factoryOf(::ReadPackageUseCase)
    factoryOf(::UpdatePackageUseCase)

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
    factoryOf(::DispatchPackageUseCase)

    single { CircuitBreakerUseCase() }
}
