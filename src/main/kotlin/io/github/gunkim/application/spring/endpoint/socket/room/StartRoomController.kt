package io.github.gunkim.application.spring.endpoint.socket.room

import io.github.gunkim.application.spring.common.id
import io.github.gunkim.application.FindRoom
import io.github.gunkim.application.StartRoom
import io.github.gunkim.domain.Room
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class StartRoomController(
    private val startRoom: StartRoom,
    private val findRoom: FindRoom,
) {
    @MessageMapping("/rooms/{roomId}/start")
    @SendTo("/topic/room/{roomId}")
    fun start(
        user: OAuth2AuthenticationToken,
        @DestinationVariable roomId: UUID
    ): Room {
        startRoom.start(roomId, user.id)

        return findRoom.find(user.id, roomId)
    }
}
