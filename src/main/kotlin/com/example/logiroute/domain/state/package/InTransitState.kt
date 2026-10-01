package com.example.logiroute.domain.state.`package`

import com.example.logiroute.domain.model.Package

class InTransitState : PackageState {

    override fun deliver(packageItem: Package) {
        packageItem.transitionTo(DeliveredState())
    }

    override fun failDelivery(packageItem: Package) {
        packageItem.transitionTo(DeliveryFailedState())
    }
}