const createPlayer = (players) => {
    return players.map(player => {
        return `
      <li class="player" pid="${player.id}">
        <div class="player-info">
          <div class="player-avatar"
               style="background-image: url(${player.profileImageUrl});"></div>
          <div>${player.host ? '&#9813;' : ''}${player.name}</div>
        </div>
        ${player.ready ? '<div class="status ready">레디</div>' : ''}
        <div class="kick">강퇴</div>
      </li>
      `;
    });
}

const leaveRoom = async (roomId) => {
    const response = await axios.delete(`/rooms/${roomId}/leave`);

    if (response.status === 200) {
        window.location.href = '/';
    }
}

const ready = async (roomId) => {
    await axios.put(`/rooms/${roomId}/ready`);
}

const start = async (roomId) => {
    try {
        await axios.put(`/rooms/${roomId}/start`);
    } catch (e) {
        alert(e.response.data.message);
    }
}

const stompClient = createStompClient((stompClient) => {
    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/rooms/${roomId}/wait`, (response) => {
            const room = JSON.parse(response.body);

            putHtml('player-list', createPlayer(room.players).join(''));
            putHtml('room-title', room.title);
        });
        stompClient.subscribe(`/topic/rooms/${roomId}/start`, () => {
            location.href = `/rooms/${roomId}/game`;
        });
    });
})
