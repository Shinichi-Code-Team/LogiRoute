package com.example.logiroute.domain.repository
import com.example.logiroute.data.dataholder.PackageRaw

interface PackageRepository {
    fun getPackages(): List<PackageRaw>
}