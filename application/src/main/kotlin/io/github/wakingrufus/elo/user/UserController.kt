package io.github.wakingrufus.elo.user

class UserController(val userPersistence: UserPersistence) {
    fun getAll(): UserListResponse {
        return UserListResponse(userPersistence.getAll().map { User(it.id, it.username) })
    }
}