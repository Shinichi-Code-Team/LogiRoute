package com.example.logiroute.com.example.logiroute.domain.usecase.model.exceptions

open class LogisticsException(message: String) : Exception(message) {
    companion object {
        const val Invalid_Capacity_Exception = "Capacity threshold must be greater than zero. Provided:"
    }

    class InvalidCapacityException(message: String) :
        LogisticsException(Invalid_Capacity_Exception)
}
