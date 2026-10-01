package com.example.logiroute.domain.state.`package`

import com.example.logiroute.domain.model.Package

class AssignedToVehicleState : PackageState {

    override fun startTransit(packageItem: Package) {
        packageItem.transitionTo(InTransitState())
    }
}