package br.com.israelbastos.springbootarchetype.controller;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;
import br.com.israelbastos.springbootarchetype.exception.notfound.NotFoundException;
import br.com.israelbastos.springbootarchetype.service.MarvelCharacterService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class MarvelCharacterControllerTest {
    @InjectMocks
    private MarvelCharacterController controller;
    @Mock
    private MarvelCharacterService serviceMock;

    @Mock
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {

        MarvelCharacter marvelCharacter = MarvelCharacterCreator.createValidMarvelCharacter();
        List<MarvelCharacter> marvelCharacterList = Arrays.asList(MarvelCharacterCreator.createValidMarvelCharacter());
        PageImpl<MarvelCharacter> marvelCharacterPage = new PageImpl<>(Arrays.asList(MarvelCharacterCreator.createValidMarvelCharacter()));

        BDDMockito.when(serviceMock.findAllNonPageable()).thenReturn(marvelCharacterList);
        BDDMockito.when(serviceMock.findAll(ArgumentMatchers.any())).thenReturn(marvelCharacterPage);
        BDDMockito.when(serviceMock.findByIdOrThrowNotFoundException(ArgumentMatchers.anyLong())).thenReturn(marvelCharacter);
        BDDMockito.when(serviceMock.findByName(ArgumentMatchers.anyString())).thenReturn(marvelCharacterList);
        BDDMockito.when(serviceMock.save(ArgumentMatchers.any(MarvelCharacterDTO.class))).thenReturn(marvelCharacter);

        BDDMockito.doNothing().when(serviceMock).delete(ArgumentMatchers.anyLong());
        BDDMockito.doNothing().when(serviceMock).put(ArgumentMatchers.any(MarvelCharacterDTO.class));
    }

    @Test
    @DisplayName("findAllNonPageable {} test the return of controller with a all marvel character no pages regards")
    void findAllNonPageable_When_Successful() {
        List<MarvelCharacter> marvelCharacters = controller.findAllNonPageable().getBody();

        Assertions.assertThat(marvelCharacters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findAll {} test the return of controller with a pageable list")
    void findAll_When_Successful() {
        Page<MarvelCharacter> marvelCharacters = controller.findAll(null).getBody();

        Assertions.assertThat(marvelCharacters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findById {} test the return of controller with a single marvel character found by id")
    void findById_When_Successful() {
        MarvelCharacter marvelCharacter = controller.findById(1).getBody();

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
    @DisplayName("findById {} edge test the return of controller when marvel character is not found by id")
    void findById_When_MarvelCharacter_Not_Found() {

        BDDMockito.when(serviceMock.findByIdOrThrowNotFoundException(ArgumentMatchers.anyLong()))
                .thenThrow(NotFoundException.class);

        Assertions.assertThatExceptionOfType(
                        NotFoundException.class)
                .isThrownBy(() -> this.controller.findById(-1).getBody());
    }

    @Test
    @DisplayName("findByName {} test the return of controller with a single marvel character found by name")
    void findByName_When_Successful() {
        List<MarvelCharacter> marvelCharacters = controller.findByName("The Winter Soldier").getBody();

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
    @DisplayName("findByName {} edge test the return of controller when marvel character is not found by name")
    void findByName_When_MarvelCharacter_Not_Found() {
        BDDMockito.when(serviceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<MarvelCharacter> marvelCharacters = controller.findByName("Not to be found").getBody();

        Assertions.assertThat(marvelCharacters)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save {} test if the post method was sent by the controller and return successful")
    void save_When_Successful() {
        ResponseEntity<MarvelCharacter> entity = controller.save(MarvelCharacterCreator.createValidMarvelCharacterDTO());

        Assertions.assertThat(entity.getBody()).isNotNull();
        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("delete {} test if the delete method was sent by the controller successfully")
    void delete_When_Successful() {
        ResponseEntity<Void> entity = controller.delete(MarvelCharacterCreator.createValidMarvelCharacterDTO().getId());

        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace {} test if the put method was sent by the controller successfully")
    void put_When_Successful() {
        ResponseEntity<Void> entity = controller.put(MarvelCharacterCreator.createValidMarvelCharacterDTO());

        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}