package io.github.wakingrufus.elo.security

import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

fun BeanDefinitionDsl.security() {
    bean<UserDetailsService> {
        EloUserDetailsService(ref("bcrypt"), ref())
    }
    bean("bcrypt") {
        BCryptPasswordEncoder()
    }
    bean<SecurityFilterChain> {
        val http = ref<HttpSecurity>()
        http {
            authorizeHttpRequests {
                authorize("/login", permitAll)
                authorize("/admin/**", hasRole("ADMIN"))
                authorize(HttpMethod.GET, "/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            httpBasic {
            }
            formLogin {
                defaultSuccessUrl("/index", false)
                permitAll()
            }
            logout {
                permitAll()
            }
            csrf {
                disable()
            }
        }
        http.build()
    }
}