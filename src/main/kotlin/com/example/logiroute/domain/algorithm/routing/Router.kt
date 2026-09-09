package com.example.logiroute.domain.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

interface Router {
    fun findRoute(
        source: Warehouse,
        destination: Warehouse,
    ): List<Warehouse>
}