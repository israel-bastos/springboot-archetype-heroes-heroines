package br.com.israelbastos.springbootarchetype.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarvelCharacterDTO {
    private Long id;
    @NotEmpty(message = "name cannot be empty or null.")
    private String name;
}