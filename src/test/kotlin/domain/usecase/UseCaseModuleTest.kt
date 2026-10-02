package domain.usecase

import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.crud.`package`.CreatePackageUseCase
import com.example.logiroute.domain.usecase.crud.route.ReadRouteUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.CreateVehicleUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertNotNull

class UseCaseModuleTest : KoinTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<PackageRepository> {
                    mockk(relaxed = true)
                }
                single<RouteRepository> {
                    mockk(relaxed = true)
                }
                single<VehicleRepository> {
                    mockk(relaxed = true)
                }
                single<WarehouseRepository> {
                    mockk(relaxed = true)
                }
            },
            validatorModule,
            useCaseModule
        )
    }

    @Test
    fun `Koin resolves registered use cases`() {
        assertNotNull(get<CreatePackageUseCase>())
        assertNotNull(get<ReadRouteUseCase>())
        assertNotNull(get<CreateVehicleUseCase>())
        assertNotNull(get<ReadWarehouseUseCase>())
    }
}