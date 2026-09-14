package com.example.logiroute.data.csv.datasource

import com.example.logiroute.data.csv.raw.PackageRaw

interface CsvPackageDataSource {
    fun getPackages(): List<PackageRaw>
}