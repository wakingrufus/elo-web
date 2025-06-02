package io.github.wakingrufus.elo.user

import java.util.UUID

@JvmRecord
data class UserRecord(val id: UUID, val username: String, val passwordHash: String, val roles: List<String>)
class UserPersistence {
    private val byId: MutableMap<UUID, UserRecord> = mutableMapOf()
    private val byName: MutableMap<String, UserRecord> = mutableMapOf()

    fun getByName(username: String): UserRecord? {
        return byName[username]
    }

    fun getById(id: UUID): UserRecord? {
        return byId[id]
    }

    fun create(name: String, passwordHash: String, roles: List<String>): UUID {
        val id = UUID.randomUUID()
        val record = UserRecord(id, name, passwordHash, roles)
        byName[name] = record
        byId[id] = record
        return id
    }

    fun getAll(): List<UserRecord> {
        return byId.values.toList()
    }
}