package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.DetectEmergencyCargoRescueRequest
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetectEmergencyCargoRescueOpportunitiesUseCaseTest {

    private val packageRepository: PackageRepository = mockk()
    private val vehicleRepository: VehicleRepository = mockk()
    private val warehouseRepository: WarehouseRepository = mockk()
    private val findOptimalPathUseCase: FindOptimalPathUseCase = mockk()

    private val useCase = DetectEmergencyCargoRescueOpportunitiesUseCase(
        packageRepository = packageRepository,
        vehicleRepository = vehicleRepository,
        warehouseRepository = warehouseRepository,
        findOptimalPathUseCase = findOptimalPathUseCase
    )

    private val warehouseA = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val warehouseB = Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)
    private val urgentPackage = Package("PKG-001", 50.0, warehouseA, warehouseB, Priority.URGENT)
    private val vehicle = Vehicle("TRK-001", 1000.0, 2.5, warehouseA)

    @Test
    fun `should return rescue opportunities when warehouse, urgent packages, vehicles, and route exist`() = runTest {
        // Given
        val request = DetectEmergencyCargoRescueRequest("WH-001")
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA, warehouseB)
        coEvery { packageRepository.getAllPackages() } returns listOf(urgentPackage)
        coEvery { vehicleRepository.getAllVehicles() } returns listOf(vehicle)
        coEvery { findOptimalPathUseCase(source = warehouseA, destination = warehouseB) } returns listOf(warehouseA, warehouseB)

        // When
        val opportunities = useCase(request)

        // Then
        assertEquals(1, opportunities.size)
        val opportunity = opportunities.first()
        assertEquals(urgentPackage, opportunity.urgentPackage)
        assertEquals(warehouseA, opportunity.currentWarehouse)
        assertEquals(warehouseB, opportunity.nextHopWarehouse)
        assertEquals(vehicle, opportunity.availableVehicle)
    }

    @Test
    fun `should throw WarehouseNotFoundException when warehouse does not exist`() = runTest {
        // Given
        val request = DetectEmergencyCargoRescueRequest("WH-999")
        coEvery { warehouseRepository.getAllWarehouses() } returns emptyList()

        // When & Then
        assertThrows<LogisticsException.WarehouseNotFoundException> {
            useCase(request)
        }
    }

    @Test
    fun `should throw NoUrgentPackagesException when no urgent packages exist at warehouse`() = runTest {
        // Given
        val request = DetectEmergencyCargoRescueRequest("WH-001")
        val standardPackage = Package("PKG-002", 30.0, warehouseA, warehouseB, Priority.STANDARD)
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA)
        coEvery { packageRepository.getAllPackages() } returns listOf(standardPackage)

        // When & Then
        assertThrows<LogisticsException.NoUrgentPackagesException> {
            useCase(request)
        }
    }

    @Test
    fun `should throw NoSuitableVehicleException when no vehicles exist at warehouse`() = runTest {
        // Given
        val request = DetectEmergencyCargoRescueRequest("WH-001")
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA)
        coEvery { packageRepository.getAllPackages() } returns listOf(urgentPackage)
        coEvery { vehicleRepository.getAllVehicles() } returns emptyList()

        // When & Then
        assertThrows<LogisticsException.NoSuitableVehicleException> {
            useCase(request)
        }
    }

    @Test
    fun `should throw NoSuitableVehicleException when optimal path has no next hop`() = runTest {
        // Given
        val request = DetectEmergencyCargoRescueRequest("WH-001")
        coEvery { warehouseRepository.getAllWarehouses() } returns listOf(warehouseA, warehouseB)
        coEvery { packageRepository.getAllPackages() } returns listOf(urgentPackage)
        coEvery { vehicleRepository.getAllVehicles() } returns listOf(vehicle)
        coEvery { findOptimalPathUseCase(source = warehouseA, destination = warehouseB) } returns listOf(warehouseA)

        // When & Then
        assertThrows<LogisticsException.NoSuitableVehicleException> {
            useCase(request)
        }
    }
}
