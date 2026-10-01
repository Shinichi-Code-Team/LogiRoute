package com.example.logiroute.domain.state.`package`

import com.example.logiroute.domain.model.Package

class CreatedState : PackageState {

    override fun assignToVehicle(packageItem: Package) {
        packageItem.transitionTo(AssignedToVehicleState())
    }
}