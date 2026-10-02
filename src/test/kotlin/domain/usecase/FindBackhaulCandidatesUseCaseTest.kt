package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.usecase.FindBackhaulCandidatesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindBackhaulCandidatesUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val useCase = FindBackhaulCandidatesUseCase(packageRepository)

    @Test
    fun `returns packages on return path that fit vehicle capacity`() = runTest {
        val currentHub = warehouse("WH-123")
        val returnDestination = warehouse("WH-456")
        val destinationOutsidePath = warehouse("WH-789")
        val differentOrigin = warehouse("WH-999")

        val suitablePackage = packageItem(
            id = "PKG-123456",
            weight = 30.0,
            origin = currentHub,
            destination = returnDestination
        )
        val packageTooHeavy = packageItem(
            id = "PKG-234567",
            weight = 80.0,
            origin = currentHub,
            destination = returnDestination
        )
        val destinationNotOnPath = packageItem(
            id = "PKG-345678",
            weight = 20.0,
            origin = currentHub,
            destination = destinationOutsidePath
        )
        val wrongOrigin = packageItem(
            id = "PKG-456789",
            weight = 20.0,
            origin = differentOrigin,
            destination = returnDestination
        )

        coEvery { packageRepository.getAllPackages() } returns listOf(
            suitablePackage,
            packageTooHeavy,
            destinationNotOnPath,
            wrongOrigin
        )

        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 50.0,
            costPerKm = 2.0,
            currentHub = currentHub
        )
        val result = useCase(
            vehicle = vehicle,
            currentHub = currentHub,
            returnPath = listOf(currentHub, returnDestination)
        )
        assertEquals(listOf(suitablePackage), result)
        coVerify(exactly = 1) { packageRepository.getAllPackages() }
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )

    private fun packageItem(
        id: String,
        weight: Double,
        origin: Warehouse,
        destination: Warehouse
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
}