package io.github.wakingrufus.elo.user

import com.github.wakingrufus.funk.htmx.template.htmxTemplate
import kotlinx.html.li
import kotlinx.html.ul

val userListTemplate  = htmxTemplate<UserListResponse> {
    ul {
        it.users.forEach {
            li { +it.name }
        }
    }
}