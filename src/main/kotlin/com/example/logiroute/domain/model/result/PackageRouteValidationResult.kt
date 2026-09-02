package com.example.logiroute.com.example.logiroute.domain.model.result
import com.example.logiroute.domain.model.Package
data class PackageRouteValidationResult(
    val validPackages: List<Package>,
    val removedPackages: List<Package>
)