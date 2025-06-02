package io.github.wakingrufus.elo.league

import java.util.UUID

@JvmRecord
data class SaveLeagueRequest(val id: UUID,
                             val startingRating: Int,
                             val xi: Int ,
                             val kFactorBase: Int,
                             val trialPeriod: Int,
                             val trialKFactorMultiplier: Int)