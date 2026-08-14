package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.repository.PackageRepository

class CSVPackageRepository(private val loader: Loader) : PackageRepository {
    override fun getPackages(): List<PackageRaw> {
        return loader.loadPackages()
    }
}