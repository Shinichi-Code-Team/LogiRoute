import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.repository.CSVPackageRepository
import com.example.logiroute.data.repository.CSVRouteRepository
import com.example.logiroute.data.repository.CSVVehicleRepository
import com.example.logiroute.data.repository.CSVWarehouseRepository
import com.example.logiroute.domain.builder.DomainGraph
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.logic.algorithm.routing.*
import com.example.logiroute.domain.logic.packagepricing.basepricing.ExpressStrategy
import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
import com.example.logiroute.domain.logic.packagepricing.servicepricing.ColdChainDecorator
import com.example.logiroute.domain.logic.packagepricing.servicepricing.ExpressInsuranceDecorator
import com.example.logiroute.domain.logic.packagepricing.servicepricing.FragileHandlingDecorator
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.CalculatePricingUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.AnalyzeTreePerformanceUseCase

fun main() {

    val loader = Loader()

    val warehouseRepository =
        CSVWarehouseRepository(loader)

    val packageRepository =
        CSVPackageRepository(
            loader,
            warehouseRepository
        )

    val routeRepository =
        CSVRouteRepository(
            loader,
            warehouseRepository
        )

    val vehicleRepository =
        CSVVehicleRepository(
            loader,
            warehouseRepository
        )

    val graphBuilder =
        DomainGraphBuilder(
            packageRepository,
            routeRepository,
            warehouseRepository,
            vehicleRepository
        )

    val domainGraph = graphBuilder.build()

    if (!isValidDomainGraph(domainGraph)) {
        return
    }

    printDomainGraphSummary(domainGraph)

    val routers =
        createRouters(warehouseRepository)

    runRoutingDemo(
        domainGraph,
        routers
    )

    runBidirectionalDemo(
        domainGraph,
        routers
    )

    runPricingDemo(domainGraph)


}


private fun isValidDomainGraph(domainGraph: DomainGraph): Boolean {
    if (domainGraph.warehouses.isEmpty()) {
        println("No warehouses found.")
        return false
    }

    if (domainGraph.packages.isEmpty()) {
        println("No packages found.")
        return false
    }

    return true
}

private fun printDomainGraphSummary(domainGraph: DomainGraph) {
    println("========== DOMAIN GRAPH ==========")
    println("Warehouses: ${domainGraph.warehouses.size}")
    println("Packages: ${domainGraph.packages.size}")
    println("Routes: ${domainGraph.routes.size}")
    println("Vehicles: ${domainGraph.vehicles.size}")

    println("\n========== WAREHOUSE RELATIONSHIPS ==========")

    domainGraph.warehouses.take(3).forEach { warehouse ->
        println(
            """
            Warehouse: ${warehouse.id}
            Packages: ${warehouse.cargoQueue.map { it.id }}
            Routes: ${warehouse.outgoingRoutes.map { it.id }}
            Vehicles: ${warehouse.stationedVehicles.map { it.id }}
            """.trimIndent()
        )

    }

}


private data class Routers(
    val bfs: BfsRouter,
    val dijkstra: DijkstraRouter,
    val bidirectionalBfs: BidirectionalBfsRouter
)
private fun createRouters(
    warehouseRepository: WarehouseRepository
): Routers {

    val pathConstructor = PathConstructor()

    return Routers(
        bfs = BfsRouter(
            warehouseRepository = warehouseRepository,
            pathConstructor = pathConstructor
        ),

        dijkstra = DijkstraRouter(
            warehousesRepository = warehouseRepository,
            pathConstructor = pathConstructor
        ),

        bidirectionalBfs = BidirectionalBfsRouter(
            warehousesRepository = warehouseRepository
        )
    )
}

private fun runRoutingDemo(
    domainGraph: DomainGraph,
    routers: Routers
) {
    val packageItem = domainGraph.packages.first()

    val bfsPath = routers.bfs.findRoute(
        packageItem.origin,
        packageItem.destination
    )

    val findOptimalPathUseCase = FindOptimalPathUseCase(routers.dijkstra)

    val dijkstraPath = findOptimalPathUseCase(
            packageItem.origin,
            packageItem.destination)

    println()
    println("========== BFS vs DIJKSTRA ==========")

    println("BFS Route:")
    printPath(bfsPath)

    println("Dijkstra Route:")
    printPath(dijkstraPath)

    compareRoutingResults(
        bfsPath = bfsPath,
        dijkstraPath = dijkstraPath
    )

    validateMultiHop(
        domainGraph = domainGraph,
        bfsRouter = routers.bfs,
        dijkstraRouter = routers.dijkstra
    )

    validateUnreachableDestination(
        domainGraph = domainGraph,
        bfsRouter = routers.bfs,
        dijkstraRouter = routers.dijkstra
    )

    findWeightedDifference(
        domainGraph = domainGraph,
        bfsRouter = routers.bfs,
        dijkstraRouter = routers.dijkstra
    )
}
private fun runBidirectionalDemo(
    domainGraph: DomainGraph,
    routers: Routers
) {
    val packageItem = domainGraph.packages.first()

    val bfsPath = routers.bfs.findRoute(
        packageItem.origin,
        packageItem.destination
    )

    val bidirectionalPath =
        routers.bidirectionalBfs.findRoute(
            packageItem.origin,
            packageItem.destination
        )

    compareBfsWithBidirectional(
        bfsPath = bfsPath,
        bidirectionalPath = bidirectionalPath,
        bidirectionalRouter = routers.bidirectionalBfs,
        bfsRouter = routers.bfs
    )
}

private fun runPricingDemo(domainGraph: DomainGraph) {
    val pricingStrategy = ExpressStrategy()

    val pricingEngine =
        RoutePricingEngine(pricingStrategy)

    val calculatePricingUseCase =
        CalculatePricingUseCase(pricingEngine)

    val packageItem =
        domainGraph.packages.first()

    val fragilePackage =
        FragileHandlingDecorator(packageItem)

    val insuredPackage =
        ExpressInsuranceDecorator(fragilePackage)

    val premiumPackage =
        ColdChainDecorator(insuredPackage)

    val basePackageCost =
        calculatePricingUseCase(
            packageItem = packageItem,
            distanceKm = 100.0
        )

    val decoratedPackageCost =
        calculatePricingUseCase(
            packageItem = packageItem,
            distanceKm = 100.0,
            packageComponent = premiumPackage
        )

    println("\n========== PACKAGE PRICING ==========")
    println("Base Package Cost = $basePackageCost")
    println("Decorated Package Cost = $decoratedPackageCost")
}

fun printPath(path: List<Warehouse>) {
    if (path.isEmpty()) {
        println("No route found.")
        return
    }

    for (index in path.indices) {
        print(path[index].name)

        if (index < path.size - 1) {
            print(" -> ")
        }
    }

}

/*
BFS finds the path with the fewest number of hops between warehouses.
It does not use Route.distanceKm when choosing the path.

Dijkstra calculates the cumulative distance of the routes and selects
the path with the smallest total distance.

Therefore, BFS cannot guarantee the shortest physical path when edge
weights vary.

For example:

A -> D = 100 km

A -> B -> C -> D
10 km + 10 km + 10 km = 30 km

BFS chooses A -> D because it contains only one hop.
Dijkstra chooses A -> B -> C -> D because its total distance is smaller.
*/
fun compareRoutingResults(bfsPath: List<Warehouse>, dijkstraPath: List<Warehouse>) {
    if (bfsPath.isEmpty()) {
        println("BFS could not find a route.")
    } else {
        println("BFS hops = ${bfsPath.size - 1}")
        println(
            "BFS distance = ${
                calculatePathDistance(bfsPath)
            } km"
        )
    }

    if (dijkstraPath.isEmpty()) {
        println("Dijkstra could not find a route.")
    } else {
        println("Dijkstra hops = ${dijkstraPath.size - 1}")
        println(
            "Dijkstra distance = ${
                calculatePathDistance(dijkstraPath)
            } km"
        )
    }

    if (
        bfsPath.isNotEmpty() &&
        dijkstraPath.isNotEmpty()
    ) {
        if (bfsPath == dijkstraPath) {
            println("Both routers selected the same path.")
        } else {
            println("BFS and Dijkstra selected different paths.")
        }
    }
}

fun calculatePathDistance(
    path: List<Warehouse>
): Double {

    var totalDistance = 0.0

    for (index in 0 until path.size - 1) {

        val currentWarehouse = path[index]
        val nextWarehouse = path[index + 1]

        val route =
            currentWarehouse.outgoingRoutes
                .firstOrNull {
                    it.destination == nextWarehouse
                }

        if (route != null) {
            totalDistance += route.distanceKm
        }
    }

    return totalDistance
}

fun validateMultiHop(
    domainGraph: DomainGraph,
    bfsRouter: Router,
    dijkstraRouter: Router
) {
    var found = false

    for (origin in domainGraph.warehouses) {
        for (destination in domainGraph.warehouses) {
            if (origin != destination) {
                val bfsPath =
                    bfsRouter.findRoute(
                        origin,
                        destination
                    )

                if (bfsPath.size > 2) {
                    println("Multi-hop scenario:")
                    println(
                        "${origin.name} -> ${destination.name}"
                    )

                    println("BFS:")
                    printPath(bfsPath)

                    val dijkstraPath =
                        dijkstraRouter.findRoute(
                            origin,
                            destination
                        )

                    println("Dijkstra:")
                    printPath(dijkstraPath)

                    found = true
                    break
                }
            }
        }

        if (found) {
            break
        }
    }

    if (!found) {
        println("No multi-hop scenario found.")
    }
}

fun validateUnreachableDestination(domainGraph: DomainGraph, bfsRouter: Router, dijkstraRouter: Router) {
    var found = false
    for (origin in domainGraph.warehouses) {
        for (destination in domainGraph.warehouses) {
            if (origin != destination) {
                val bfsPath = bfsRouter.findRoute(origin, destination)

                if (bfsPath.isEmpty()) {
                    println("Unreachable scenario:")
                    println("${origin.name} -> ${destination.name}")
                    println("BFS: No route found.")
                    val dijkstraPath = dijkstraRouter.findRoute(origin, destination)

                    if (dijkstraPath.isEmpty()) {
                        println("Dijkstra: No route found.")
                    } else {
                        println("Dijkstra:")
                        printPath(dijkstraPath)
                    }
                    found = true
                    break
                }
            }
        }
        if (found) {
            break
        }
    }
    if (!found) {
        println("No unreachable scenario found.")
    }
}

private fun findWeightedDifference(domainGraph: DomainGraph, bfsRouter: Router, dijkstraRouter: Router) {
    var found = false

    for (origin in domainGraph.warehouses) {
        for (destination in domainGraph.warehouses) {
            if (origin != destination) {
                val bfsPath = bfsRouter.findRoute(origin, destination)
                val dijkstraPath = dijkstraRouter.findRoute(origin, destination)

                if (
                    bfsPath.isNotEmpty() &&
                    dijkstraPath.isNotEmpty()
                ) {
                    val bfsDistance = calculatePathDistance(bfsPath)

                    val dijkstraDistance = calculatePathDistance(dijkstraPath)

                    if (
                        bfsPath != dijkstraPath &&
                        dijkstraDistance < bfsDistance
                    ) {
                        println("Weighted graph comparison:")
                        println("${origin.name} -> ${destination.name}")
                        println("BFS:")
                        printPath(bfsPath)
                        println("Hops = ${bfsPath.size - 1}")
                        println("Distance = $bfsDistance km")
                        println("Dijkstra:")
                        printPath(dijkstraPath)
                        println("Hops = ${dijkstraPath.size - 1}")
                        println("Distance = $dijkstraDistance km")
                        found = true
                        break
                    }
                }
            }
        }

        if (found) {
            break
        }
    }

    if (!found) {
        println("No weighted difference found in current data.")
    }
}


fun calculateHops(
    path: List<Warehouse>
): Int {
    return if (path.isNotEmpty()) {
        path.size - 1
    } else {
        0
    }
}

fun printBfsResult(
    path: List<Warehouse>,
    evaluatedNodes: Int
) {
    println()
    println("Standard BFS:")
    printPath(path)
    println()
    println("Hops = ${calculateHops(path)}")
    println("Evaluated Nodes = $evaluatedNodes")
}

fun printBidirectionalResult(
    path: List<Warehouse>,
    router: BidirectionalBfsRouter
) {
    println()
    println("Bidirectional BFS:")
    printPath(path)
    println()
    println("Hops = ${calculateHops(path)}")
    println(
        "Evaluated Nodes = ${router.lastEvaluatedNodesCount}"
    )
}

fun verifySameHopCount(
    bfsPath: List<Warehouse>,
    bidirectionalPath: List<Warehouse>
) {

    println("----------------------------------------------")

    when {
        bfsPath.isEmpty() && bidirectionalPath.isEmpty() -> {
            println(
                "Both algorithms could not find a route."
            )
        }

        bfsPath.isEmpty() -> {
            println("BFS could not find a route.")
            println("Bidirectional BFS found a route.")
        }

        bidirectionalPath.isEmpty() -> {
            println(
                "Bidirectional BFS could not find a route."
            )
            println("BFS found a route.")
        }

        calculateHops(bfsPath) ==
                calculateHops(bidirectionalPath) -> {

            println("Verification: PASSED")
            println(
                "Both algorithms found a shortest-hop path."
            )
        }

        else -> {
            println("Verification: FAILED")
            println(
                "The algorithms returned different hop counts."
            )
        }
    }
    // ========== SUB-TASK 4: BALANCED INDEX SIMULATOR ==========

    val analyzeTreePerformanceUseCase = AnalyzeTreePerformanceUseCase()
    val performanceReport = analyzeTreePerformanceUseCase()

    println()
    println("Unbalanced BST height: ${performanceReport.unbalancedHeight}")
    println("Balanced tree height:  ${performanceReport.balancedHeight}")
    performanceReport.sampleKeys.forEach { key ->
        println(
            "$key -> unbalanced steps: ${performanceReport.unbalancedSteps[key]}, " +
                    "balanced steps: ${performanceReport.balancedSteps[key]}"
        )
    }

}

fun compareBfsWithBidirectional(
    bfsPath: List<Warehouse>,
    bidirectionalPath: List<Warehouse>,
    bidirectionalRouter: BidirectionalBfsRouter,
    bfsRouter: BfsRouter
) {
    println()
    println("==============================================")
    println("BFS vs BIDIRECTIONAL BFS")
    println("==============================================")


    printBfsResult(bfsPath, bfsRouter.evaluatedNodes)
    printBidirectionalResult(
        path = bidirectionalPath,
        router = bidirectionalRouter
    )

    verifySameHopCount(
        bfsPath = bfsPath,
        bidirectionalPath = bidirectionalPath
    )


}


