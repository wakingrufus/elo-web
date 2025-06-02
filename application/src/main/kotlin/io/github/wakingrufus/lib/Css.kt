package io.github.wakingrufus.lib

import kotlinx.css.CssBuilder

fun css(builder: CssBuilder.() -> Unit): String {
    return CssBuilder().apply(builder).toString()
}