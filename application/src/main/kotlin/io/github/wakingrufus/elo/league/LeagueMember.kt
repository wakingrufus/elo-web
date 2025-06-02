package io.github.wakingrufus.elo.league

import java.util.UUID

@JvmRecord
data class LeagueMember(val leagueId: UUID, val name: String, val role: Role)
enum class Role{ADMIN, MEMBER}
