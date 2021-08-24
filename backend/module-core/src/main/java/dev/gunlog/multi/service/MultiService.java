package dev.gunlog.multi.service;

import dev.gunlog.multi.domain.GameRoomRepository;
import dev.gunlog.multi.domain.UserRoomRepository;
import dev.gunlog.multi.model.Game;
import dev.gunlog.multi.model.GameRoom;
import dev.gunlog.multi.model.Player;
import dev.gunlog.room.domain.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class MultiService {
    private final GameRoomRepository gameRoomRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;

    public GameRoom leftMove(String username) {
        return this.commonMove(username, game -> game.leftMove());
    }
    public GameRoom rightMove(String username) {
        return this.commonMove(username, game -> game.rightMove());
    }
    public GameRoom topMove(String username) {
        return this.commonMove(username, game -> game.topMove());
    }
    public GameRoom bottomMove(String username) {
        return this.commonMove(username, game -> game.bottomMove());
    }
    public GameRoom findRoomByRoomId(Integer roomId) {
        return gameRoomRepository.findRoomByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게임 방을 찾을 수 없습니다. ROOM_ID : "+roomId));
    }
    public Integer findRoomId(String memberId) {
        return userRoomRepository.findRoomIdByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("참가한 게임 방을 찾을 수 없습니다."));
    }
    public boolean gameStart(String memberId) {
        Integer roomId = userRoomRepository.findRoomIdByMemberId(memberId)
                .orElseThrow();
        GameRoom room = gameRoomRepository.findRoomByRoomId(roomId)
                .orElseThrow();
        if(memberId.equals(room.getHost())) {
            room.gameStart();
            for (Player player : room.getPlayers()) {
                if(player.getGameInfo() == null) {
                    player.setGameInfo(new Game());
                }
            }
            return true;
        }
        return false;
    }
    @Transactional
    public void exitRoom(String memberId) {
        Integer roomId = userRoomRepository.findRoomIdByMemberId(memberId)
                .orElseThrow();
        GameRoom room = gameRoomRepository.findRoomByRoomId(roomId)
                .orElseThrow();

        room.setPlayers(room.getPlayers().stream()
                .filter(player -> !memberId.equals(player.getNickname()))
                .collect(Collectors.toList()));
        userRoomRepository.deleteByMemberId(memberId);

        if(room.getPlayers().size() == 0) {
            gameRoomRepository.deleteByRoomId(roomId);
            roomRepository.deleteById(Long.valueOf(roomId));
        }
    }
    private GameRoom commonMove(String username, Consumer<Game> gameConsumer) {
        Integer roomId = userRoomRepository.findRoomIdByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 들어가 있는 방을 찾을 수 없습니다. USERNAME : "+username));
        GameRoom room = gameRoomRepository.findRoomByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게임 방을 찾을 수 없습니다. ROOM_ID : "+roomId));

        if(!room.isStart()) {
            return room;
        }

        Stream<Player> players = room.getPlayers().stream();
        Player myPlayer = players
                .filter(player -> username.equals(player.getNickname()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("플레이어를 찾을 수 없습니다 : "+username));

        gameConsumer.accept(myPlayer.getGameInfo());

        return room;
    }
}