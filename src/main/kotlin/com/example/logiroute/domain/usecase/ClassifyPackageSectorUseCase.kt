package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import kotlin.math.pow
import kotlin.math.sqrt

data class ClassifySectorRequest(
    val latitude: Double,
    val longitude: Double,
    val weight: Double
)

class ClassifyPackageSectorUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(
        request: ClassifySectorRequest,
        k: Int = 5
    ): Result<String> = runCatching {
        val historicalPackages = packageRepository.getAllPackages()

        if (historicalPackages.size < k) {
            throw IllegalArgumentException(
                "KNN classification requires at least $k historical records, but found ${historicalPackages.size}."
            )
        }

        val bounds = calculateFeatureBounds(request, historicalPackages)
        val targetPoint = createNormalizedPoint(request.latitude, request.longitude, request.weight, bounds)

        historicalPackages
            .map { historyPkg ->
                val historyPoint = createNormalizedPoint(
                    historyPkg.origin.latitude,
                    historyPkg.origin.longitude,
                    historyPkg.weight,
                    bounds
                )
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

    private data class FeatureBounds(
        val minLat: Double, val maxLat: Double,
        val minLon: Double, val maxLon: Double,
        val minWeight: Double, val maxWeight: Double
    )

    private data class NormalizedPoint(
        val lat: Double, val lon: Double, val weight: Double
    )

    private fun calculateFeatureBounds(
        target: ClassifySectorRequest,
        history: List<Package>
    ): FeatureBounds {
        val allLats = history.map { it.origin.latitude } + target.latitude
        val allLons = history.map { it.origin.longitude } + target.longitude
        val allWeights = history.map { it.weight } + target.weight

        return FeatureBounds(
            minLat = allLats.min(), maxLat = allLats.max(),
            minLon = allLons.min(), maxLon = allLons.max(),
            minWeight = allWeights.min(), maxWeight = allWeights.max()
        )
    }

    private fun createNormalizedPoint(
        lat: Double,
        lon: Double,
        weight: Double,
        bounds: FeatureBounds
    ): NormalizedPoint = NormalizedPoint(
        lat = normalize(lat, bounds.minLat, bounds.maxLat),
        lon = normalize(lon, bounds.minLon, bounds.maxLon),
        weight = normalize(weight, bounds.minWeight, bounds.maxWeight)
    )

    private fun normalize(value: Double, min: Double, max: Double): Double =
        if (max == min) 0.0 else (value - min) / (max - min)

    private fun calculateDistance(p1: NormalizedPoint, p2: NormalizedPoint): Double =
        sqrt((p1.lat - p2.lat).pow(2) + (p1.lon - p2.lon).pow(2) + (p1.weight - p2.weight).pow(2))
}
