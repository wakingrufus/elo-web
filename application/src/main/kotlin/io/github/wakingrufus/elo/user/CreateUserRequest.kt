package io.github.wakingrufus.elo.user

@JvmRecord
data class CreateUserRequest(val name: String, val password: String)