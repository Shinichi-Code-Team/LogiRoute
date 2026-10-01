package com.example.logiroute.domain.state.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.exceptions.LogisticsException

interface PackageState {

    fun assignToVehicle(packageItem: Package) {
        invalidTransition(packageItem, "assign to vehicle")
    }

    fun startTransit(packageItem: Package) {
        invalidTransition(packageItem, "start transit")
    }

    fun deliver(packageItem: Package) {
        invalidTransition(packageItem, "deliver")
    }

    fun failDelivery(packageItem: Package) {
        invalidTransition(packageItem, "fail delivery")
    }

    fun invalidTransition(
        packageItem: Package,
        action: String
    ) {
        throw LogisticsException.InvalidPackageStateTransitionException(
            packageId = packageItem.id,
            fromState = this::class.simpleName ?: "UnknownState",
            action = action
        )
    }
}