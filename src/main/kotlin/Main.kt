package org.example

import com.example.logiroute.dataholder.PackageRow
import com.example.logiroute.logic.sorting.sortPackagesByPriority
import packageParser

fun printTopPackages(packages: List<PackageRow>) {

    println("Successfully parsed packages: ${packages.size}")

    for (i in 0 until minOf(3, packages.size)) {

        val packageRow = packages[i]

        println(
            "ID: ${packageRow.id}, " +
                    "Weight: ${packageRow.weight}, " +
                    "Destination: ${packageRow.destinationHubId}, " +
                    "Priority: ${packageRow.priority}"
        )
    }
}

fun processPackages() {

    val packages = packageParser()
    sortPackagesByPriority(packages)
    printTopPackages(packages)
}

fun main() {
    processPackages()

}

