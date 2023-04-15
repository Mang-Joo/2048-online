package io.github.gunkim.game.application.endpoint.http.room.response

import io.github.gunkim.game.domain.Gamer
import io.github.gunkim.game.domain.Room
import java.util.UUID

data class WaitRoomResponse(
    val id: UUID,
    val title: String,
    val players: List<PlayerResponse>,
) {
    constructor(room: Room) : this(
        room.id,
        room.title,
        room.gamers.map(::PlayerResponse),
    )
}

data class PlayerResponse(
    val id: UUID,
    val name: String,
    val profileImageUrl: String?,
    val isReady: Boolean,
    val isHost: Boolean
) {
    constructor(gamer: Gamer) : this(
        gamer.user.id,
        gamer.user.name,
        gamer.user.profileImageUrl,
        true,
        gamer.isHost,
    )
}
