package br.com.israelbastos.springbootarchetype.repository;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.util.MarvelCharacterCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class MarvelCharacterRepositoryTest {

    @Autowired
    private MarvelCharacterRepository repository;

    @Test
    @DisplayName("save {} test if the marvel character was persisted successful in the database")
    void save_Persist_MarvelCharacter() {
        MarvelCharacter savedMarvelCharacter = this.repository.save(MarvelCharacterCreator.createMarvelCharacterToSave());

        Assertions.assertThat(savedMarvelCharacter).isNotNull();
    }

    @Test
    @DisplayName("findById {} test if the marvel character was found successful in the database")
    void findById_Persisted_MarvelCharacter() {
        MarvelCharacter savedMarvelCharacter = this.repository.save(MarvelCharacterCreator.createMarvelCharacterToSave());
        Optional<MarvelCharacter> findSavedMarvelCharacter = this.repository.findById(savedMarvelCharacter.getId());

        Assertions.assertThat(Optional.of(savedMarvelCharacter)).isEqualTo(findSavedMarvelCharacter);
    }

    @Test
    @DisplayName("update {} test if the marvel character was updated successful in the database")
    void save_Update_MarvelCharacter() {
        MarvelCharacter savedMarvelCharacter = this.repository.save(MarvelCharacterCreator.createMarvelCharacterToSave());

        String nameSaved = savedMarvelCharacter.getName();

        savedMarvelCharacter.setName("Ant-Man");

        Optional<MarvelCharacter> findSavedMarvelCharacter = this.repository.findById(savedMarvelCharacter.getId());
        MarvelCharacter updatedMarvelCharacter = this.repository.save(savedMarvelCharacter);

        Assertions.assertThat(findSavedMarvelCharacter.map(MarvelCharacter::getName)).isNotEqualTo(Optional.of(nameSaved));
        Assertions.assertThat(findSavedMarvelCharacter.map(MarvelCharacter::getId)).isEqualTo(Optional.of(updatedMarvelCharacter.getId()));
    }

    @Test
    @DisplayName("delete {} test if the marvel character was deleted successful in the database")
    void save_Delete_MarvelCharacter() {
        MarvelCharacter savedMarvelCharacter = this.repository.save(MarvelCharacterCreator.createMarvelCharacterToSave());
        this.repository.delete(savedMarvelCharacter);

        List<MarvelCharacter> searching = this.repository.findByName(savedMarvelCharacter.getName());

        Assertions.assertThat(savedMarvelCharacter).isNotIn(searching);
    }

    @Test
    @DisplayName("findByName {} test if the marvel character was successful found by name in the database")
    void findByName_MarvelCharacterPersisted() {
        MarvelCharacter savedMarvelCharacter = this.repository.save(MarvelCharacterCreator.createMarvelCharacterToSave());
        List<MarvelCharacter> searching = this.repository.findByName(savedMarvelCharacter.getName());

        Assertions.assertThat(savedMarvelCharacter).isIn(searching);
    }

    @Test
    @DisplayName("findByName {} test if the marvel character was not found and returned empty list in the database")
    void notFindByName_MarvelCharacterPersisted() {
        List<MarvelCharacter> searching = this.repository.findByName("not_found");

        Assertions.assertThat(searching).isEmpty();
    }

    @Test
    @DisplayName("save {} test if the name of the marvel character is empty or null do not save in the database and throw " +
            "ConstraintViolationException")
    void save_ThrowsConstraintViolationException() {
        MarvelCharacter marvelCharacterNull = new MarvelCharacter();

        Assertions.assertThatExceptionOfType(
                ConstraintViolationException.class)
                .isThrownBy(() -> this.repository.save(marvelCharacterNull))
                .withMessageContaining("name cannot be null or empty.");
    }
}