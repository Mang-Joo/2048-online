package io.github.gunkim.application.endpoint.socket.room.request

import io.github.gunkim.domain.vo.MoveType

data class MoveBoardRequest(val direction: MoveType = MoveType.UNKNOWN)
