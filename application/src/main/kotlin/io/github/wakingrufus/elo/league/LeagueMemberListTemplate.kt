package io.github.wakingrufus.elo.league

import com.github.wakingrufus.funk.htmx.template.htmxTemplate
import io.github.wakingrufus.lib.css
import kotlinx.css.ListStyleType
import kotlinx.css.listStyleType
import kotlinx.css.paddingRight
import kotlinx.css.px
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.ul

val leagueMemberListTemplate = htmxTemplate<LeagueMemberList> {
    ul {
        style = css {
            listStyleType = ListStyleType.none
        }
        it.members.forEach {
            li {
                span {
                    style = css {
                        paddingRight = 8.px
                    }
                    +it.name
                }
                span { +it.role.toString() }
            }
        }
    }
}