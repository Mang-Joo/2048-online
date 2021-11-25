package dev.gunlog.room.dto;

import dev.gunlog.room.domain.enums.Mode;
import dev.gunlog.room.domain.enums.Personnel;
import dev.gunlog.room.domain.Room;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoomListResponseDto {
    private Long id;
    private String title;
    private String username;
    private Mode mode;
    private Personnel personnel;
    private int participant;

    public RoomListResponseDto(Room room) {
        this.id = room.getId();
        this.title = room.getTitle();
        this.username = room.getMember().getNickname();
        this.mode = room.getMode();
        this.personnel = room.getPersonnel();
    }
    @Builder
    public RoomListResponseDto(Long id, String title, String username, Mode mode, Personnel personnel, int participant) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.mode = mode;
        this.personnel = personnel;
        this.participant = participant;
    }
}