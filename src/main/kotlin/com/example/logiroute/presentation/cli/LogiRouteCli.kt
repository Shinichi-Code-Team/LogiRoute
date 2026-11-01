package com.example.logiroute.presentation.cli

class LogiRouteCli(
    private val routeCli: RouteCli,
    private val packageCli: PackageCli,
    private val fleetCli: FleetCli,
    private val lastMileCli: LastMileCli
) {
    suspend fun run() {
        while (true) {
            printMenu()
            when (readCliInput("Choose a CLI:") ?: "0") {
                "1" -> safelyRunCliCommand { routeCli.run() }
                "2" -> safelyRunCliCommand { packageCli.run() }
                "3" -> safelyRunCliCommand { fleetCli.run() }
                "4" -> safelyRunCliCommand { lastMileCli.run() }
                "0" -> return
                else -> println("Unknown option. Choose one of the listed CLIs.")
            }
        }
    }

    private fun printMenu() {
        println()
        println("=== LogiRoute ===")
        println("1. Route CLI")
        println("2. Package CLI")
        println("3. Fleet telemetry CLI")
        println("4. Last-mile optimization CLI")
        println("0. Exit")
    }
}
