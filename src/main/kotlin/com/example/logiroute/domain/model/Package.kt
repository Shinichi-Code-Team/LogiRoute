package com.example.logiroute.domain.model

import com.example.logiroute.domain.pricingPackage.servicepricing.PackageComponent
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.state.`package`.CreatedState
import com.example.logiroute.domain.state.`package`.PackageState
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationRules

data class Package  constructor(
     val id: String,
     val weight: Double,
     val origin: Warehouse,
     val destination: Warehouse,
     val priority: Priority
) : PackageComponent  {

    init {
        val errors = listOfNotNull(
            ValidationRules.validatePackageId(id),

            ValidationRules.validatePositiveDouble(
                value = weight,
                field = ValidationField.WEIGHT
            ),

            ValidationRules.validateWarehouseId(
                value = origin.id,
                field = ValidationField.ORIGIN_HUB_ID
            ),

            ValidationRules.validateWarehouseId(
                value = destination.id,
                field = ValidationField.DESTINATION_HUB_ID
            )
        )

        if (errors.isNotEmpty()) {
            throw LogisticsException.EntityValidationException(errors)
        }
    }

    override fun calculateCost(baseCost: Double): Double {
        return baseCost
    }
    var state: PackageState = CreatedState()
        private set

    fun assignToVehicle() {
        state.assignToVehicle(this)
    }

    fun startTransit() {
        state.startTransit(this)
    }

    fun deliver() {
        state.deliver(this)
    }

    fun failDelivery() {
        state.failDelivery(this)
    }

    fun transitionTo(newState: PackageState) {
        state = newState
    }
}