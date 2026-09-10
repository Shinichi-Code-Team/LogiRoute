package com.example.logiroute.data.datasource

import com.example.logiroute.data.dataholder.PackageRaw

interface PackageDataSource {
    fun getPackages(): List<PackageRaw>
}