package io.github.wakingrufus.elo.league

@JvmRecord
data class LeagueListResponse(val leagues: List<LeagueName>, val canCreate:Boolean)