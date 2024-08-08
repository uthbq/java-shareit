package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    @NotNull(groups = Marker.Update.class)
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private LocalDateTime created;
}
