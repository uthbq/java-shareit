package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {
    @Size(max = 100)
    @NotBlank
    private String name;
    @Size(max = 200)
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
