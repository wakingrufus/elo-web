package io.github.wakingrufus.elo.league

import com.github.wakingrufus.elo.League
import java.util.UUID

class LeaguePersistence {
    private val leagues: MutableMap<UUID, League> = mutableMapOf()
    private val leagueNames: MutableMap<UUID, String> = mutableMapOf()
    private val members: MutableMap<UUID, MutableMap<String, LeagueMember>> = mutableMapOf()

    fun createLeague(name: String) : UUID {
        val id = UUID.randomUUID()
        leagues[id] = League()
        leagueNames[id] = name
        members[id] = mutableMapOf()
        return id
    }

    fun getLeagueSettings(uuid: UUID): League? {
        return leagues[uuid]
    }

    fun save(id: UUID, league: League) {
        leagues[id] = league
    }

    fun getLeagueName(uuid: UUID): String? {
        return leagueNames[uuid]
    }

    fun listLeagues(): List<Pair<UUID, String>> {
        return leagueNames.entries.map { it.toPair() }
    }

    fun getMember(leagueId: UUID, name: String): LeagueMember? {
        return members[leagueId]?.get(name)
    }

    fun getMembers(leagueId: UUID): List<LeagueMember> {
        return members[leagueId]?.values?.toList() ?: emptyList()
    }

    fun addMember(leagueId: UUID, name: String, role: Role){
        members[leagueId]?.put(name, LeagueMember(leagueId, name, role))
    }
}