package io.github.wakingrufus.elo.user

import java.util.UUID

@JvmRecord
data class User(val id: UUID, val name: String)