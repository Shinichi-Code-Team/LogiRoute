package com.example.logiroute.com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse

data class ConsolidationOpportunityRequest(
    val mainPackage: Package,
    val compatiblePackages: List<Package>,
    val sharedRoute: List<Warehouse>
)