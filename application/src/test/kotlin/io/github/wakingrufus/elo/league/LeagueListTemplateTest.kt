package io.github.wakingrufus.elo.league

import com.github.wakingrufus.elo.League
import kotlinx.html.stream.appendHTML
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import java.util.UUID

class LeagueListTemplateTest {
    private val log = KotlinLogging.logger {}

    @Test
    fun test() {
        val actual = buildString {
            leagueListTemplate.render(
                appendHTML(false),
                LeagueListResponse(listOf(LeagueName(UUID.randomUUID(), "test")), false)
            )
        }
        log.info { actual }
    }

    @Test
    fun `test league detail view`() {
        val actual = buildString {
            leagueDetailTemplate.render(
                appendHTML(false), LeagueSuccessResponse(UUID.randomUUID(), "name", League(), false)
            )
        }
        log.info { actual }
    }
}
