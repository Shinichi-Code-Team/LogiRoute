import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.util.suspendRunCatching

class AddVehicleToHubUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Result<Vehicle> {

        return suspendRunCatching {
            vehicleRepository.addVehicle(vehicle)
        }
    }
}