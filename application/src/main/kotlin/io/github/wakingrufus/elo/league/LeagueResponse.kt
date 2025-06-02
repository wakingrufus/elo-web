package io.github.wakingrufus.elo.league

import com.github.wakingrufus.elo.League
import java.util.UUID

sealed interface LeagueResponse

@JvmRecord
data class LeagueSuccessResponse(
    val id: UUID,
    val name: String,
    val settings: League,
    val canEdit: Boolean
) : LeagueResponse

data object LeagueFailureResponse : LeagueResponse