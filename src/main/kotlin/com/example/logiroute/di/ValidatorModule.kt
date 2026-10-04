package com.example.logiroute.di

import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validator.AtLeastOneFieldValidator
import com.example.logiroute.domain.validator.PackageUpdateValidator
import com.example.logiroute.domain.validator.RouteUpdateValidator
import com.example.logiroute.domain.validator.VehicleUpdateValidator
import com.example.logiroute.domain.validator.WarehouseUpdateValidator
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val validatorModule = module {
    singleOf(::ValidationRules)
    singleOf(::ValidationResultMapper)
    singleOf(::AtLeastOneFieldValidator)

    factoryOf(::PackageUpdateValidator)
    factoryOf(::RouteUpdateValidator)
    factoryOf(::VehicleUpdateValidator)
    factoryOf(::WarehouseUpdateValidator)
}