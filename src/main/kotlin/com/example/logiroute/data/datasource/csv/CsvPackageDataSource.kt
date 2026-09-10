package com.example.logiroute.data.datasource.csv

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.datasource.PackageDataSource
import com.example.logiroute.data.processing.loader.Loader

class CsvPackageDataSource(
    private val loader: Loader
) : PackageDataSource {

    override fun getPackages(): List<PackageRaw> {
        return loader.loadPackages()
    }
}