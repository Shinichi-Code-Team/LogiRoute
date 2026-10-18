package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.result.PackageRouteValidationResult

class ValidatePackagesAgainstFinalRouteUseCase {
    operator fun invoke(
        packages: List<Package>,
        finalRoutePath: List<Warehouse>,
        mainPackage: Package
    ): PackageRouteValidationResult {
        val (validPackages, removedPackages) = packages.partition {
            it.destination in finalRoutePath
        }
        check(validPackages.any { it.id == mainPackage.id }) {
            "Main package destination is not part of the selected route."
        }
        return PackageRouteValidationResult(validPackages, removedPackages)
    }
}