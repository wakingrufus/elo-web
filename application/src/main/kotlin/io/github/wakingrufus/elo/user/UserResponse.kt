package io.github.wakingrufus.elo.user

sealed interface UserResponse

@JvmRecord
data class UserSuccess(val user: User) : UserResponse
data object UserFailure : UserResponse