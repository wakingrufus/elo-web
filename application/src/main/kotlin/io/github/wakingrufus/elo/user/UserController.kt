package io.github.wakingrufus.elo.user

import org.springframework.security.crypto.password.PasswordEncoder

class UserController(
    private val encoder: PasswordEncoder,
    private val userPersistence: UserPersistence) {
    fun getAll(): UserListResponse {
        return UserListResponse(userPersistence.getAll().map { User(it.id, it.username) })
    }

    fun create(request: CreateUserRequest): UserResponse {
        val id = userPersistence.create(request.name, encoder.encode(request.password), listOf("USER"))
        return userPersistence.getById(id)?.let {
            UserSuccess(User(it.id,it.username))
        } ?: UserFailure
    }
}