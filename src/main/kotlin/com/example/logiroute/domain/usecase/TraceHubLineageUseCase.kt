package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.HubNode


class TraceHubLineageUseCase {

    operator fun invoke(
        hub: HubNode
    ): List<HubNode> =
        generateSequence(hub) { it.parentHub }
            .toList()
}