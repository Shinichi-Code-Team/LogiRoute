package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemotePackageDataSource
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.mapper.PackageDtoMapper
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val remoteDataSource: RemotePackageDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: PackageDtoMapper
) : PackageRepository {

    override fun getAllPackages(): List<Package> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return remoteDataSource
            .getPackages()
            .mapNotNull { dto ->
                mapRemotePackage(
                    dto = dto,
                    warehouseMap = warehouseMap
                )
            }
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