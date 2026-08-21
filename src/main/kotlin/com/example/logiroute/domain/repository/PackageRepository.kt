package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Package


interface PackageRepository {
    fun getAllPackages(): List<Package>
}