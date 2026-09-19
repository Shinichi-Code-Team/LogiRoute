import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class AddVehicleToHubUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Result<Vehicle> {

        return runCatching {
            vehicleRepository.addVehicle(vehicle)
        }
    }
}