package io.github.wakingrufus.elo.user

import com.github.wakingrufus.funk.htmx.template.htmxTemplate
import kotlinx.html.li

val userListItemTemplate = htmxTemplate<User> {
    li { +it.name }
}