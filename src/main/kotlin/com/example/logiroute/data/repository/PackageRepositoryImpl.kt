package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemotePackageDataSource
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.mapper.PackageDtoMapper
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val remoteDataSource: RemotePackageDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: PackageDtoMapper
) : PackageRepository {

    override suspend fun getAllPackages(): List<Package> {
        val warehouseMap = getWarehouseMap()

        return remoteDataSource
            .getPackages()
            .mapNotNull { dto ->
                mapRemotePackage(dto, warehouseMap)
            }
    }

    override suspend fun getPackageById(id: String): Package? {
        val dto = remoteDataSource.getPackageById(id)
            ?: return null

        return mapRemotePackage(
            dto = dto,
            warehouseMap = getWarehouseMap()
        )
    }

    override suspend fun createPackage(
        packageItem: Package
    ): Package {
        val request = dtoMapper.toCreateRequest(packageItem)
        val dto = remoteDataSource.createPackage(request)

        return mapRemotePackage(
            dto = dto,
            warehouseMap = getWarehouseMap()
        ) ?: error("Unable to resolve package warehouse references")
    }

    override suspend fun updatePackage(
        id: String,
        input: UpdatePackageInput
    ): Package {

        val request = dtoMapper.toUpdateRequest(input)

        val dto = remoteDataSource.updatePackage(
            id = id,
            request = request
        )

        return mapRemotePackage(
            dto = dto,
            warehouseMap = getWarehouseMap()
        ) ?: error("Unable to resolve package warehouse references")
    }

    override suspend fun deletePackage(id: String) {
        remoteDataSource.deletePackage(id)
    }

    private suspend fun getWarehouseMap(): Map<String, Warehouse> {
        return warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
    }

    private fun mapRemotePackage(
        dto: PackageResponseDto,
        warehouseMap: Map<String, Warehouse>
    ): Package? {
        val origin = warehouseMap[dto.originHubId]
            ?: return null

        val destination = warehouseMap[dto.destinationHubId]
            ?: return null

        val packageItem = dtoMapper.toDomain(
            dto = dto,
            origin = origin,
            destination = destination
        )

        origin.addPackage(packageItem)

        return packageItem
    }
}