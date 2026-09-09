package com.example.logiroute.domain.model.result

sealed class AssignmentResult {
    data object Success : AssignmentResult()
    data object AlreadyQueued : AssignmentResult()
    data object OriginMismatch : AssignmentResult()
}