package br.com.israelbastos.springbootarchetype.service;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;
import br.com.israelbastos.springbootarchetype.exception.notfound.NotFoundException;
import br.com.israelbastos.springbootarchetype.repository.MarvelCharacterRepository;
import br.com.israelbastos.springbootarchetype.util.DateUtil;
import br.com.israelbastos.springbootarchetype.util.MarvelCharacterCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class MarvelCharacterServiceTest {
    @InjectMocks
    private MarvelCharacterService service;
    @Mock
    private MarvelCharacterRepository repositoryMock;
    @BeforeEach
    void setUp() {

        MarvelCharacter marvelCharacter = MarvelCharacterCreator.createValidMarvelCharacter();

        List<MarvelCharacter> marvelCharacterList = Arrays.asList(MarvelCharacterCreator.createValidMarvelCharacter());
        PageImpl<MarvelCharacter> marvelCharacterPage = new PageImpl<>(Arrays.asList(MarvelCharacterCreator.createValidMarvelCharacter()));

        BDDMockito.when(repositoryMock.findAll()).thenReturn(marvelCharacterList);
        BDDMockito.when(repositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(marvelCharacterPage);
        BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(marvelCharacter));
        BDDMockito.when(repositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(marvelCharacterList);
        BDDMockito.when(repositoryMock.save(ArgumentMatchers.any(MarvelCharacter.class))).thenReturn(marvelCharacter);

        BDDMockito.doNothing().when(repositoryMock).delete(ArgumentMatchers.any(MarvelCharacter.class));
    }

    @Test
    @DisplayName("findAllNonPageable {} test the return of controller with a all marvel characters no pages regards")
    void findAllNonPageable_When_Successful() {
        List<MarvelCharacter> marvelCharacters = service.findAllNonPageable();

        Assertions.assertThat(marvelCharacters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findAll {} test the return of controller with a pageable list")
    void findAll_When_Successful() {
        Page<MarvelCharacter> marvelCharacters = service.findAll(PageRequest.of(0, 3));

        Assertions.assertThat(marvelCharacters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findById {} test the return of controller with a single marvel characters found by id")
    void findById_When_Successful() {
        MarvelCharacter marvelCharacter = service.findByIdOrThrowNotFoundException(1);

        Assertions.assertThat(marvelCharacter)
                .isNotNull();

        Assertions.assertThat(marvelCharacter.getId())
                .isNotNull()
                .isEqualTo(MarvelCharacterCreator.createValidMarvelCharacter().getId());

        Assertions.assertThat(marvelCharacter.getName())
                .isNotNull()
                .isEqualTo(MarvelCharacterCreator.createValidMarvelCharacter().getName());
    }

    @Test
    @DisplayName("findById {} edge test the return of service when marvel characters is not found by id")
    void findById_When_MarvelCharacter_Not_Found() {

        BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenThrow(NotFoundException.class);

        Assertions.assertThatExceptionOfType(
                        NotFoundException.class)
                .isThrownBy(() -> this.service.findByIdOrThrowNotFoundException(-1));
    }

    @Test
    @DisplayName("findByName {} test the return of service with a single marvel characters found by name")
    void findByName_When_Successful() {
        List<MarvelCharacter> marvelCharacters = service.findByName("Dororo");

        Assertions.assertThat(marvelCharacters)
                .isNotEmpty()
                .isNotNull();

        Assertions.assertThat(marvelCharacters.get(0).getId())
                .isNotNull()
                .isEqualTo(MarvelCharacterCreator.createValidMarvelCharacter().getId());

        Assertions.assertThat(marvelCharacters.get(0).getName())
                .isNotNull()
                .isEqualTo(MarvelCharacterCreator.createValidMarvelCharacter().getName());
    }

    @Test
    @DisplayName("findByName {} edge test the return of service when marvel characters is not found by name")
    void findByName_When_MarvelCharacter_Not_Found() {

        BDDMockito.when(repositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<MarvelCharacter> marvelCharacters = service.findByName("Not to be found");

        Assertions.assertThat(marvelCharacters)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save {} test if the save was return successful")
    void save_When_Successful() {
        MarvelCharacter marvelCharacter = service.save(MarvelCharacterCreator.createValidMarvelCharacterDTO());

        Assertions.assertThat(marvelCharacter).isNotNull();
    }

    @Test
    @DisplayName("delete {} test if the delete returns successful")
    void delete_When_Successful() {
        MarvelCharacterDTO marvelCharacterDTO = MarvelCharacterCreator.createValidMarvelCharacterDTO();
        service.delete(marvelCharacterDTO.getId());

        Assertions.assertThatCode(() -> service.delete(marvelCharacterDTO.getId())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("replace {} test if the put returns successful")
    void put_When_Successful() {
        MarvelCharacterDTO marvelCharacterDTO = MarvelCharacterCreator.createValidMarvelCharacterDTO();
        service.put(marvelCharacterDTO);

        Assertions.assertThatCode(() -> service.put(marvelCharacterDTO)).doesNotThrowAnyException();
    }
}