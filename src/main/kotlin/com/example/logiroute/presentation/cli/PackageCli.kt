package com.example.logiroute.presentation.cli

import com.example.logiroute.domain.usecase.ClassifyPackageSectorUseCase
import com.example.logiroute.domain.usecase.ClassifySectorRequest

class PackageCli(
    private val classifyPackageSectorUseCase: ClassifyPackageSectorUseCase
) {
    suspend fun run() {
        while (true) {
            printMenu()
            when (readCliInput("Choose a package command:") ?: "0") {
                "1" -> safelyRunCliCommand { classifyPackage() }
                "0" -> return
                else -> println("Unknown option. Choose one of the listed commands.")
            }
        }
    }

    private fun printMenu() {
        println()
        println("=== Package CLI ===")
        println("1. Classify package sector (KNN)")
        println("0. Back to main menu")
    }

    private suspend fun classifyPackage() {
        val latitude = readFiniteCliDouble("Package latitude:")
        val longitude = readFiniteCliDouble("Package longitude:")
        val weight = readFiniteCliDouble("Package weight in kg:")

        if (latitude == null || longitude == null || weight == null) {
            println("Enter valid finite numbers for latitude, longitude, and weight.")
            return
        }

        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            println("Latitude or longitude is outside its valid range.")
            return
        }

        if (weight <= 0.0) {
            println("Package weight must be greater than zero.")
            return
        }

        val request = ClassifySectorRequest(latitude, longitude, weight)
        classifyPackageSectorUseCase(request).fold(
            onSuccess = { sector -> println("Predicted delivery sector: $sector") },
            onFailure = { error -> println("Package classification failed: ${error.message}") }
        )
    }
}
