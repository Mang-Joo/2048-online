package io.github.gunkim.game.domain

import java.util.UUID

interface Users {
    fun find(): List<User>
    fun find(id: UUID): User
    fun findByEmail(email: String): User?
    fun save(user: User): User
}
