package com.example.logiroute.di
import com.example.logiroute.domain.validator.AtLeastOneFieldValidator
import com.example.logiroute.domain.validator.PackageUpdateValidator
import com.example.logiroute.domain.validator.RouteUpdateValidator
import com.example.logiroute.domain.validator.VehicleUpdateValidator
import com.example.logiroute.domain.validator.WarehouseUpdateValidator
import org.koin.dsl.module
val validatorModule = module {
    single {
        AtLeastOneFieldValidator()
    }

    factory {
        PackageUpdateValidator(
            atLeastOneFieldValidator = get()
        )
    }

    factory {
        RouteUpdateValidator(
            atLeastOneFieldValidator = get()
        )
    }

    factory {
        VehicleUpdateValidator(
            atLeastOneFieldValidator = get()
        )
    }

    factory {
        WarehouseUpdateValidator(
            atLeastOneFieldValidator = get()
        )
    }
}