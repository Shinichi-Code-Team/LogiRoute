package domain.usecase

import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.util.crud.`package`.CreatePackageUseCase
import com.example.logiroute.domain.util.crud.route.ReadRouteUseCase
import com.example.logiroute.domain.util.crud.vehicle.CreateVehicleUseCase
import com.example.logiroute.domain.util.crud.warehouse.ReadWarehouseUseCase
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.core.qualifier.named
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
        assertNotNull(get<com.example.logiroute.domain.util.crud.`package`.CreatePackageUseCase>())
        assertNotNull(get<com.example.logiroute.domain.util.crud.route.ReadRouteUseCase>())
        assertNotNull(get<com.example.logiroute.domain.util.crud.vehicle.CreateVehicleUseCase>())
        assertNotNull(get<com.example.logiroute.domain.util.crud.warehouse.ReadWarehouseUseCase>())
        assertNotNull(get<FindOptimalPathUseCase>())
        assertNotNull(get<FindOptimalPathUseCase>(named("delayRouter")))
    }
}
