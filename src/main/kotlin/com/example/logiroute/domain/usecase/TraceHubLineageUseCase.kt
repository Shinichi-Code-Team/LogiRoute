package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.HubNode

class TraceHubLineageUseCase {

    operator fun invoke(leafHub: HubNode): List<HubNode> =
        generateSequence(leafHub) { it.parentHub }
            .toList()
}