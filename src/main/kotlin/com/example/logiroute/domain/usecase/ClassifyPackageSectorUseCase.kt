package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.FeatureBounds
import com.example.logiroute.domain.model.NormalizedPoint
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import kotlin.math.pow
import kotlin.math.sqrt

class ClassifyPackageSectorUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        packageId: String,
        k: Int = 5
    ): Result<String> = runCatching {
        val allPackages = packageRepository.getAllPackages()

        val targetPackage = allPackages.find { it.id == packageId }
            ?: throw IllegalArgumentException("Package not found with ID: $packageId")

        val historicalPackages = allPackages.filter { it.id != packageId }

        if (historicalPackages.size < k) {
            throw IllegalArgumentException(
                "KNN classification requires at least $k historical records, but found ${historicalPackages.size}."
            )
        }

        val bounds = calculateFeatureBounds(targetPackage, historicalPackages)
        val targetPoint = createNormalizedPoint(targetPackage, bounds)

        historicalPackages
            .map { historyPkg ->
                val historyPoint = createNormalizedPoint(historyPkg, bounds)
                val distance = calculateDistance(targetPoint, historyPoint)
                Pair(historyPkg.origin.regionalZone, distance)
            }
            .sortedBy { (_, distance) -> distance }
            .take(k)
            .groupingBy { (sector, _) -> sector }
            .eachCount()
            .maxByOrNull { (_, count) -> count }
            ?.key ?: "CENTRAL"
    }

    private fun calculateFeatureBounds(
        target: Package,
        history: List<Package>
    ): FeatureBounds {
        val allLats = history.map { it.origin.latitude } + target.origin.latitude
        val allLons = history.map { it.origin.longitude } + target.origin.longitude
        val allWeights = history.map { it.weight } + target.weight

        return FeatureBounds(
            minLat = allLats.min(), maxLat = allLats.max(),
            minLon = allLons.min(), maxLon = allLons.max(),
            minWeight = allWeights.min(), maxWeight = allWeights.max()
        )
    }

    private fun createNormalizedPoint(
        pkg: Package,
        bounds: FeatureBounds
    ): NormalizedPoint = NormalizedPoint(
        lat = normalize(pkg.origin.latitude, bounds.minLat, bounds.maxLat),
        lon = normalize(pkg.origin.longitude, bounds.minLon, bounds.maxLon),
        weight = normalize(pkg.weight, bounds.minWeight, bounds.maxWeight)
    )

    private fun normalize(value: Double, min: Double, max: Double): Double =
        if (max == min) 0.0 else (value - min) / (max - min)

    private fun calculateDistance(p1: NormalizedPoint, p2: NormalizedPoint): Double =
        sqrt((p1.lat - p2.lat).pow(2) + (p1.lon - p2.lon).pow(2) + (p1.weight - p2.weight).pow(2))
}
