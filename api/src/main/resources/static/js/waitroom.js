const createPlayer = (players) => {
  return players.map(player => {
    return `
      <li class="player" pid="${player.id}">
        <div class="player-info">
          <div class="player-avatar"
               style="background-image: url(${player.profileImageUrl});"></div>
          <div>${player.host ? '&#9813;':''}${player.name}</div>
        </div>
        ${player.host && player.ready ? '<div class="status ready">레디</div>' : '<div class="status">언레디</div>'}
        <div class="kick">강퇴</div>
      </li>
      `;
  });
}

const stompClient = (() => {
  const socket = new SockJS("/websocket");
  const stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    stompClient.subscribe(`/topic/rooms/${roomId}/wait`, (response) => {
      const rooms = JSON.parse(response.body);

      putHtml(createPlayer(rooms));
    });
  });

  return stompClient;
})();

const roomId = document.location.href.split('/')[4];

const putHtml = (id, html) => {
  document.getElementById(id).innerHTML = html;
}

(async () => {
  const response = await axios.get(`/rooms/${roomId}/wait`);

  putHtml('player-list', createPlayer(response.data.players));
  putHtml('room-title', response.data.title);
})();
