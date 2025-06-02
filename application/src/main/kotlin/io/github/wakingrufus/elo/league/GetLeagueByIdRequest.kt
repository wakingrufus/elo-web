package io.github.wakingrufus.elo.league

import java.util.UUID

@JvmRecord
data class GetLeagueByIdRequest(val id: UUID)