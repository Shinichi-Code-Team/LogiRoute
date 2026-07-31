package com.example.logiroute.data.processing.loader

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.data.processing.parser.*


fun loaderPackages(): List<PackageRaw> {
    val lines = readCsvLines("packages.csv")
    return parsePackages(lines)
}

fun loaderRoutes(): List<RouteRaw> {
    val lines = readCsvLines("routes.csv")
    return parseRoutes(lines)
}

fun loaderFleet(): List<FleetRaw> {
    val lines = readCsvLines("fleet.csv")
    return parseFleets(lines)
}

fun loaderWarehouses(): List<WarehouseRaw> {
    val lines = readCsvLines("warehouses.csv")
    return parseWarehouses(lines)
}