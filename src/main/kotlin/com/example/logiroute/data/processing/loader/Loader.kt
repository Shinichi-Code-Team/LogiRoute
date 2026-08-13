package com.example.logiroute.data.processing.loader

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.data.processing.parser.*


class Loader {
    fun loadPackages(): List<PackageRaw> {
        val lines = readCsvLines("packages.csv")
        return parsePackages(lines)
    }

    fun loadRoutes(): List<RouteRaw> {
        val lines = readCsvLines("routes.csv")
        return parseRoutes(lines)
    }

    fun loadFleets(): List<FleetRaw> {
        val lines = readCsvLines("fleet.csv")
        return parseFleets(lines)
    }

    fun loadWarehouses(): List<WarehouseRaw> {
        val lines = readCsvLines("warehouses.csv")
        return parseWarehouses(lines)
    }
}