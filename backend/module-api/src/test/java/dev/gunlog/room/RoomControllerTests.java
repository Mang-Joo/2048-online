package dev.gunlog.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gunlog.config.SecurityConfig;
import dev.gunlog.member.domain.Role;
import dev.gunlog.model.LoginRequest;
import dev.gunlog.room.domain.Mode;
import dev.gunlog.room.domain.Personnel;
import dev.gunlog.room.dto.RoomCreateRequestDto;
import dev.gunlog.room.dto.RoomListResponseDto;
import dev.gunlog.room.service.RoomService;
import dev.gunlog.service.CustomUserDetailsService;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RoomService roomService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        given(customUserDetailsService.loadUserByUsername("gunkim"))
                .willReturn(new User("gunkim", "$2a$10$jHc8ndvWho7p/a2/kVvfJOVgYiC1rLy9nT2ddRjUfzulh4/6vYXyC",
                        List.of(new SimpleGrantedAuthority(Role.USER.name()))));

        final LoginRequest loginRequest = new LoginRequest("gunkim", "gunkim");
        final String content = objectMapper.writeValueAsString(loginRequest);
        MvcResult result = mockMvc.perform(post("/api/v2/member/signIn")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        this.token = result.getResponse().getContentAsString().replaceAll("\"", "");
    }
    @Test
    @DisplayName("방 목록 조회 테스트")
    public void roomsTest() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of(
                RoomListResponseDto.builder()
                        .id(1L)
                        .title("테스트 방1")
                        .mode(Mode.SPEED_ATTACK)
                        .username("gunkim")
                        .personnel(Personnel.FOUR)
                        .build()
        ));

        mockMvc.perform(get("/api/v2/room/list")
                .header(SecurityConfig.AUTHENTICATION_HEADER_NAME, this.token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].mode").value(Mode.SPEED_ATTACK.getTitle()))
                .andExpect(jsonPath("$[0].title").value("테스트 방1"))
                .andExpect(jsonPath("$[0].personnel").value(Personnel.FOUR.getSize()))
                .andExpect(jsonPath("$[0].username").value("gunkim"));
    }
    @Test
    @DisplayName("방 생성 테스트")
    public void createRoomTest() throws Exception {
        RoomCreateRequestDto requestDto = RoomCreateRequestDto.builder()
                .title("테스트 방1")
                .mode(Mode.SURVIVAL)
                .personnel(Personnel.FOUR)
                .build();
        String content = objectMapper.writeValueAsString(requestDto).replaceAll("서바이벌", "SURVIVAL").replaceAll("4", "\"FOUR\"");

        mockMvc.perform(post("/api/v2/room")
                .header(SecurityConfig.AUTHENTICATION_HEADER_NAME, this.token)
                .content(content))
                .andDo(print())
                .andExpect(status().isOk());
    }
}