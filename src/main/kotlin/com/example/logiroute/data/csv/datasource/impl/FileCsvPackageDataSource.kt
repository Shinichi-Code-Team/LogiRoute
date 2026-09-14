package com.example.logiroute.data.csv.datasource.impl

import com.example.logiroute.data.csv.datasource.CsvPackageDataSource
import com.example.logiroute.data.csv.processing.loader.Loader
import com.example.logiroute.data.csv.raw.PackageRaw

class FileCsvPackageDataSource(
    private val loader: Loader
) : CsvPackageDataSource {

    override fun getPackages(): List<PackageRaw> {
        return loader.loadPackages()
    }
}