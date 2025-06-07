package io.github.wakingrufus.elo

import com.github.wakingrufus.funk.base.SpringFunkApplication
import com.github.wakingrufus.funk.beans.beans
import com.github.wakingrufus.funk.core.SpringDslContainer
import com.github.wakingrufus.funk.htmx.HttpVerb
import com.github.wakingrufus.funk.htmx.htmx
import com.github.wakingrufus.funk.htmx.hxGet
import com.github.wakingrufus.funk.htmx.hxPushUrl
import com.github.wakingrufus.funk.htmx.swap.HxSwapType
import com.github.wakingrufus.funk.htmx.template.template
import com.github.wakingrufus.funk.logging.logging
import com.github.wakingrufus.funk.webmvc.webmvc
import io.github.wakingrufus.elo.league.LeagueController
import io.github.wakingrufus.elo.league.LeagueName
import io.github.wakingrufus.elo.league.LeaguePersistence
import io.github.wakingrufus.elo.league.LeagueSuccessResponse
import io.github.wakingrufus.elo.league.leagueDetailTemplate
import io.github.wakingrufus.elo.league.leagueEditTemplate
import io.github.wakingrufus.elo.league.leagueListItemTemplate
import io.github.wakingrufus.elo.league.leagueListTemplate
import io.github.wakingrufus.elo.league.leagueMemberListTemplate
import io.github.wakingrufus.elo.security.security
import io.github.wakingrufus.elo.user.UserController
import io.github.wakingrufus.elo.user.UserPersistence
import io.github.wakingrufus.elo.user.UserSuccess
import io.github.wakingrufus.elo.user.userListItemTemplate
import io.github.wakingrufus.elo.user.userListTemplate
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.li
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.logging.LogLevel

@EnableAutoConfiguration(
    exclude = [
        WebMvcAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class
    ]
)
@SpringBootConfiguration
open class EloApplication : SpringFunkApplication {
    override fun dsl(): SpringDslContainer.() -> Unit = {
        logging {
            root(LogLevel.INFO)
            level("io.github.wakingrufus", LogLevel.INFO)
        }
        beans {
            bean<LeaguePersistence>()
            bean<LeagueController>()
            bean<UserPersistence>()
            bean<UserController>()
            security()
        }
        htmx {
            page("/index") {
                div {
                    button {
                        hxGet("/leagues") {
                            swap(HxSwapType.OuterHtml)
                            hxPushUrl(true)
                        }
                        +"View All Leagues"
                    }
                }
                div {
                    a(href = "/admin") {+"Admin"}
                }
            }
            get("/leagues", LeagueController::getAll, leagueListTemplate)
            route(HttpVerb.POST, "/leagues", LeagueController::create) {
                when (it) {
                    is LeagueSuccessResponse -> template(leagueListItemTemplate, LeagueName(it.id, it.name))
                    else -> li { +"Error" }
                }
            }
            route(HttpVerb.GET, "/league/{id}", LeagueController::getById, leagueDetailTemplate)
            route(HttpVerb.GET, "/league/{id}/edit", LeagueController::getById, leagueEditTemplate)
            route(HttpVerb.PUT, "/league/{id}", LeagueController::save, leagueDetailTemplate)
            route(HttpVerb.GET, "/league/{id}/members", LeagueController::getMembers, leagueMemberListTemplate)

            page("/admin") {
                div {
                    h1 { +"Users" }
                    div {
                        hxGet("/admin/users") {
                            trigger {
                                load()
                            }
                        }
                    }
                }
            }
            get("/admin/users", UserController::getAll, userListTemplate)
            route(HttpVerb.POST, "/admin/users", UserController::create) {
                when (it) {
                    is UserSuccess -> template(userListItemTemplate, it.user)
                    else -> li { +"Error" }
                }
            }
        }
        webmvc {
            enableWebMvc {
                jetty()
            }
            converters {
                jackson()
                form()
                string()
            }
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(EloApplication::class.java, *args)
}