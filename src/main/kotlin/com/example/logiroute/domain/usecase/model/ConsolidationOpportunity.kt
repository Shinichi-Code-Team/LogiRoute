package com.example.logiroute.domain.usecase.model

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse

data class ConsolidationOpportunity(
    val mainPackage: Package,
    val compatiblePackages: List<Package>,
    val sharedRoute: List<Warehouse>
)