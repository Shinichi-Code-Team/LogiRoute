import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.repository.*
import com.example.logiroute.domain.builder.*
import com.example.logiroute.domain.logic.algorithm.routing.*
import com.example.logiroute.domain.logic.packagepricing.basepricing.*
import com.example.logiroute.domain.logic.packagepricing.servicepricing.*
import com.example.logiroute.domain.model.Warehouse

fun main() {
    val loader = Loader()
    val packagesRepository = CSVPackageRepository(loader)
    val routesRepository = CSVRouteRepository(loader)
    val warehouseRepository = CSVWarehouseRepository(loader)
    val vehicleRepository = CSVVehicleRepository(loader)

    val graphBuilder = DomainGraphBuilder(
        packagesRepository,
        routesRepository, warehouseRepository, vehicleRepository
    )

    val input = DomainGraphInput(
        warehouseRaws = warehouseRepository.getWarehouses(),
        packageRaws = packagesRepository.getPackages(),
        routeRaws = routesRepository.getRoutes(),
        vehicleRaws = vehicleRepository.getVehicles()
    )

    val domainGraph = graphBuilder.build(input)
    if (domainGraph.warehouses.isEmpty()) {
        println("No warehouses found.")
        return
    }

    if (domainGraph.packages.isEmpty()) {
        println("No packages found.")
        return
    }

    val pathConstructor = PathConstructor()

    val bfsAdjacencyMap = domainGraph.warehouses.associateWith { warehouse ->
        warehouse.outgoingRoutes.map { route ->
            route.destination
        }
    }

    val weightedAdjacencyMap = domainGraph.warehouses.associateWith { warehouse ->
        warehouse.outgoingRoutes
    }

    val backwardAdjacencyMap =
        domainGraph.warehouses.associateWith {
            mutableListOf<Warehouse>()
        }

    domainGraph.warehouses.forEach { warehouse ->

        warehouse.outgoingRoutes.forEach { route ->

            backwardAdjacencyMap[route.destination]
                ?.add(warehouse)
        }
    }

    val bfsRouter = BfsRouter(bfsAdjacencyMap, pathConstructor)

    val dijkstraRouter = DijkstraRouter(weightedAdjacencyMap, pathConstructor)
    val bidirectionalBfsRouter =
        BidirectionalBfsSolver(
            forwardAdjacencyMap = bfsAdjacencyMap,
            backwardAdjacencyMap = backwardAdjacencyMap
        )

    val pricingStrategy = ExpressStrategy()

    val pricingEngine = RoutePricingEngine(pricingStrategy)

    val pricingService = DecoratedPackagePricingService(pricingEngine)

    val packageItem = domainGraph.packages[0]

    val fragilePackage = FragileHandlingDecorator(packageItem)

    val insuredPackage = ExpressInsuranceDecorator(fragilePackage)

    val premiumPackage = ColdChainDecorator(insuredPackage)

    val bfsPath = bfsRouter.findRoute(packageItem.origin, packageItem.destination)

    val dijkstraPath = dijkstraRouter.findRoute(packageItem.origin, packageItem.destination)


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
        bfsRouter = bfsRouter,
        dijkstraRouter = dijkstraRouter
    )

    validateUnreachableDestination(
        domainGraph = domainGraph,
        bfsRouter = bfsRouter,
        dijkstraRouter = dijkstraRouter
    )

    findWeightedDifference(
        domainGraph = domainGraph,
        bfsRouter = bfsRouter,
        dijkstraRouter = dijkstraRouter
    )
    val bidirectionalPath =
        bidirectionalBfsRouter.findRoute(
            packageItem.origin,
            packageItem.destination
        )

    compareBfsWithBidirectional(
        bfsPath = bfsPath,
        bidirectionalPath = bidirectionalPath,
        bidirectionalRouter = bidirectionalBfsRouter,
        bfsRouter = bfsRouter
    )

    val basePackageCost = pricingService.calculatePackageCost(
        packageComponent = packageItem,
        distanceKm = 100.0,
        weight = packageItem.weight,
        priority = packageItem.priority
    )

    val decoratedPackageCost = pricingService.calculatePackageCost(
        packageComponent = premiumPackage,
        distanceKm = 100.0,
        weight = packageItem.weight,
        priority = packageItem.priority
    )
    println("\n========== PACKAGE PRICING ==========")
    println("Base Package Cost = $basePackageCost")
    println("Decorated Package Cost = $decoratedPackageCost")




    domainGraph.warehouses.forEach { warehouse ->
        warehouse.outgoingRoutes.forEach { route ->
            backwardAdjacencyMap[route.destination]
                ?.add(warehouse)
        }

    }
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
    router: BidirectionalBfsSolver
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
}

fun compareBfsWithBidirectional(
    bfsPath: List<Warehouse>,
    bidirectionalPath: List<Warehouse>,
    bidirectionalRouter: BidirectionalBfsSolver,
    bfsRouter: BfsRouter
) {
    println()
    println("==============================================")
    println("BFS vs BIDIRECTIONAL BFS")
    println("==============================================")


    printBfsResult(bfsPath,bfsRouter.evaluatedNodes)
    printBidirectionalResult(
        path = bidirectionalPath,
        router = bidirectionalRouter
    )

    verifySameHopCount(
        bfsPath = bfsPath,
        bidirectionalPath = bidirectionalPath
    )


}

