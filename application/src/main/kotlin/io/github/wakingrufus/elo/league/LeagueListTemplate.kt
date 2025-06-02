package io.github.wakingrufus.elo.league

import com.github.wakingrufus.funk.htmx.hxGet
import com.github.wakingrufus.funk.htmx.hxPost
import com.github.wakingrufus.funk.htmx.hxPushUrl
import com.github.wakingrufus.funk.htmx.hxPut
import com.github.wakingrufus.funk.htmx.swap.HxSwapType
import com.github.wakingrufus.funk.htmx.template.htmxTemplate
import com.github.wakingrufus.funk.htmx.template.template
import io.github.wakingrufus.lib.css
import kotlinx.css.BorderStyle
import kotlinx.css.Cursor
import kotlinx.css.Display
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.cursor
import kotlinx.css.display
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.paddingBottom
import kotlinx.css.paddingLeft
import kotlinx.css.paddingRight
import kotlinx.css.paddingTop
import kotlinx.css.px
import kotlinx.html.FIELDSET
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.fieldSet
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.ol
import kotlinx.html.span
import kotlinx.html.style

val leagueListItemTemplate = htmxTemplate<LeagueName> {
    div {
        hxGet("/league/${it.id}") {
            target = "body"
        }
        hxPushUrl(true)
        style = css {
            display = Display.inlineBlock
            borderStyle = BorderStyle.solid
            borderWidth = 1.px
            borderRadius = 4.px
            paddingLeft = 8.px
            paddingRight = 8.px
            paddingTop = 4.px
            paddingBottom = 4.px
            cursor = Cursor.pointer
            marginLeft = 2.px
            marginRight = 2.px
        }
        +it.name
    }
}
val leagueListTemplate = htmxTemplate<LeagueListResponse> {
    h1 { +"League List" }
    ol {
        id = "list"
        it.leagues.forEach {
            template(leagueListItemTemplate, it)
        }
    }
    if (it.canCreate) {
        div {
            form {
                hxPost("/leagues") {
                    target = "#list"
                    swap(HxSwapType.BeforeEnd)
                }
                input(name = "name") {
                    type = InputType.text
                }
                button {
                    +"Create"
                }
            }
        }
    } else {
        div {
            a(href = "/login") { +"Log in" }
            span { +" in order to create a league." }
        }
    }
}
val leagueDetailTemplate = htmxTemplate<LeagueResponse> {
    when (it) {
        is LeagueFailureResponse -> {
            div { +"error" }
        }

        is LeagueSuccessResponse -> {
            h1 {
                +it.name
            }
            div {
                a(href = "/leagues") {
                    hxGet("/leagues") {
                        target = "body"
                        swap(HxSwapType.InnerHtml)
                        hxPushUrl(true)
                    }
                    +"View all leagues"
                }
            }
            div {
                button(name = "Edit") {
                    hxGet("/league/${it.id}/edit") {
                        target = "body"
                        noParams()
                    }
                    hxPushUrl(true)
                    +"Edit"
                }
            }
            div {
                span { +"K Factor" }
                span { +it.settings.kFactorBase.toString() }
            }
            div {
                label { +"Xi" }
                span { +it.settings.xi.toString() }
            }
            div {
                label { +"Trial Period" }
                span { +it.settings.trialPeriod.toString() }
            }
            div {
                label { +"Multiplier" }
                span { +it.settings.trialKFactorMultiplier.toString() }
            }
            div {
                label { +"Starting Rating" }
                span { +it.settings.startingRating.toString() }
            }
            div {
                h2 { +"Members" }
                div {
                    hxGet("/league/${it.id}/members") {
                        trigger {
                            load()
                        }
                    }
                    img(classes = "htmx-indicator") {
                        width = "150"
                    }
                }
            }
        }
    }
}
val leagueEditTemplate = htmxTemplate<LeagueResponse> {
    when (it) {
        is LeagueFailureResponse -> {
            div { +"error" }
        }

        is LeagueSuccessResponse -> {
            h1 {
                +it.name
            }
            form {
                hxPut("/league/${it.id}") {
                    target = "body"
                    hxPushUrl(true)
                }
                fieldSet {
                    div {
                        label {
                            htmlFor = "kFactorBase"
                            +"K Factor"
                        }
                        input(type = InputType.number, name = "kFactorBase") {
                            value = it.settings.kFactorBase.toString()
                        }
                    }
                    numberFieldEdit("Xi", "xi", it.settings.xi)
                    numberFieldEdit("Trial Period", "trialPeriod", it.settings.trialPeriod)
                    numberFieldEdit("Multiplier", "trialKFactorMultiplier", it.settings.trialKFactorMultiplier)
                    numberFieldEdit("Starting Rating", "startingRating", it.settings.startingRating)
                }
                button(name = "Save") {
                    +"Save"
                }
            }
        }
    }
}

fun FIELDSET.numberFieldEdit(label: String, name: String, initialValue: Int) {
    div {
        label {
            htmlFor = name
            +label
        }
        input(type = InputType.number, name = name) {
            value = initialValue.toString()
        }
    }
}