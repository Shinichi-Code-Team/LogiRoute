package com.example.logiroute.di

import AddVehicleToHubUseCase
import com.example.logiroute.com.example.logiroute.domain.usecase.ValidatePackagesAgainstFinalRouteUseCase
import com.example.logiroute.domain.model.command.CommandHistoryStore
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

    factory {
        FindOptimalPathUseCase(
            warehousesRepository = get(),
            routeRepository = get(),
            routeWeight = { route -> route.distanceKm }
        )
    }

    factory(named("delayRouter")) {
        FindOptimalPathUseCase(
            warehousesRepository = get(),
            routeRepository = get(),
            routeWeight = { route ->
                route.typicalDelayMin.toDouble()
            }
        )
    }

    factoryOf(::FindFewestHopsRouteUseCase)
    factoryOf(::FindFewestHopsBidirectionalRouteUseCase)

    factory {
        SelectShipmentRouteUseCase(
            distancePathUseCase = get(),
            delayPathUseCase = get(named("delayRouter")),
            fewestHopsRouteUseCase = get()
        )
    }

    single { RoutePricingEngine(EcoStrategy()) }

    single { CommandHistoryStore() }
    factoryOf(::ExecuteLogisticsCommandUseCase)
    factoryOf(::UndoLogisticsCommandUseCase)
    factoryOf(::RedoLogisticsCommandUseCase)

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
    factoryOf(::OptimizeCargoPackingUseCase)
    factoryOf(::PrioritizeShipmentConsolidationUseCase)
    factoryOf(::ReassignPackagesAfterBreakdownUseCase)
    factoryOf(::RebalanceVehicleLoadsUseCase)
    factoryOf(::RoutePackageUseCase)
    factoryOf(::SortPackagesByPriorityAndWeightUseCase)
    factoryOf(::SortCargoQueueByWeightUseCase)
    factoryOf(::TraceHubLineageUseCase)
    factoryOf(::ValidatePackagesAgainstFinalRouteUseCase)

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
}