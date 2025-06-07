package io.github.wakingrufus.elo.user

import com.github.wakingrufus.funk.htmx.hxPost
import com.github.wakingrufus.funk.htmx.swap.HxSwapType
import com.github.wakingrufus.funk.htmx.template.htmxTemplate
import com.github.wakingrufus.funk.htmx.template.template
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.ul

val userListTemplate  = htmxTemplate<UserListResponse> {
    ul {
        id = "userList"
        it.users.forEach {
            template(userListItemTemplate,it)
        }
    }
    div {
        form {
            hxPost("/admin/users") {
                target = "#userList"
                swap(HxSwapType.BeforeEnd)
            }
            input(name = "name") {
                type = InputType.text
            }
            input(name = "password") {
                type = InputType.password
            }
            button {
                +"Create"
            }
        }
    }
}