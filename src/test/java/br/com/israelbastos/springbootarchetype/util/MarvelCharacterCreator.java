package br.com.israelbastos.springbootarchetype.util;
import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;

public class MarvelCharacterCreator {
    public static MarvelCharacter createMarvelCharacterToSave() {
        return MarvelCharacter.builder()
                .name("Thor")
                .build();
    }

    public static MarvelCharacter createValidMarvelCharacter() {
        return MarvelCharacter.builder()
                .id(1L)
                .name("Spider-Man")
                .build();
    }

    public static MarvelCharacterDTO createValidMarvelCharacterDTO() {
        return MarvelCharacterDTO.builder()
                .id(1L)
                .name("The Winter Soldier")
                .build();
    }

    public static MarvelCharacter createMarvelCharacterToUpdate() {
        return MarvelCharacter.builder()
                .id(1L)
                .name("The Hulk")
                .build();
    }
}