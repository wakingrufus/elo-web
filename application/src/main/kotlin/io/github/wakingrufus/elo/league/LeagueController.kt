package io.github.wakingrufus.elo.league

import com.github.wakingrufus.elo.League
import mu.KotlinLogging
import java.security.Principal

class LeagueController(private val leaguePersistence: LeaguePersistence) {
    private val log = KotlinLogging.logger {}
    fun getAll(user: Principal?): LeagueListResponse {
        log.info { "Getting all leagues" }
        return LeagueListResponse(
            leagues = leaguePersistence.listLeagues().map { LeagueName(it.first, it.second) },
            canCreate = user != null
        )
    }

    fun getById(user: Principal?, request: GetLeagueByIdRequest): LeagueResponse {
        val name = leaguePersistence.getLeagueName(request.id) ?: return LeagueFailureResponse
        val settings = leaguePersistence.getLeagueSettings(request.id) ?: return LeagueFailureResponse
        val canEdit = (user != null) && Role.ADMIN == leaguePersistence.getMember(request.id, user.name)?.role
        return LeagueSuccessResponse(request.id, name, settings, canEdit)
    }

    fun create(user: Principal?, request: CreateLeagueRequest): LeagueResponse {
        log.info { "creating new league ${request.name}" }
        if (user == null) {
            return LeagueFailureResponse
        }
        val id = leaguePersistence.createLeague(request.name)
        leaguePersistence.addMember(id, user.name, Role.ADMIN)
        val settings = leaguePersistence.getLeagueSettings(id) ?: return LeagueFailureResponse
        return LeagueSuccessResponse(id, request.name, settings, true)
    }

    fun save(user: Principal?, request: SaveLeagueRequest): LeagueResponse {
        leaguePersistence.save(request.id, request.toLeague())
        val name = leaguePersistence.getLeagueName(request.id) ?: return LeagueFailureResponse
        val settings = leaguePersistence.getLeagueSettings(request.id) ?: return LeagueFailureResponse
        val canEdit = (user != null) && Role.ADMIN == leaguePersistence.getMember(request.id, user.name)?.role
        return LeagueSuccessResponse(request.id, name, settings, canEdit)
    }

    fun getMembers(request: GetLeagueByIdRequest): LeagueMemberList {
        return LeagueMemberList(leaguePersistence.getMembers(request.id))
    }

    private fun SaveLeagueRequest.toLeague(): League {
        return League(startingRating, xi, kFactorBase, trialPeriod, trialKFactorMultiplier)
    }
}