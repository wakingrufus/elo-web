package io.github.wakingrufus.elo.security

import io.github.wakingrufus.elo.user.UserPersistence
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

class EloUserDetailsService(
    val encoder: PasswordEncoder,
    val userPersistence: UserPersistence
) : UserDetailsService {
    private val log = KotlinLogging.logger {}

    init {
        if (userPersistence.getByName("user") == null) {
            val defaultName = "user"
            log.info { "Creating default user named '$defaultName' with password 'default'" }
            userPersistence.create(defaultName, encoder.encode("default"), listOf("USER", "ADMIN"))
        }
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException("Username is null")
        }
        val user = userPersistence.getByName(username)
        if (user != null) {
            return User(
                username, user.passwordHash, true, true,
                true, true, user.roles.map { SimpleGrantedAuthority("ROLE_$it") })
        } else {
            throw UsernameNotFoundException("User $username not found")
        }
    }
}