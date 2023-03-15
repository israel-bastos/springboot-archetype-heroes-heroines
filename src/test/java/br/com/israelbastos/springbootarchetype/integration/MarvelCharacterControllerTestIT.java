package br.com.israelbastos.springbootarchetype.integration;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.SystemUserAccess;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;
import br.com.israelbastos.springbootarchetype.repository.MarvelCharacterRepository;
import br.com.israelbastos.springbootarchetype.repository.SystemUserRepository;
import br.com.israelbastos.springbootarchetype.util.MarvelCharacterCreator;
import br.com.israelbastos.springbootarchetype.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MarvelCharacterControllerTestIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateAdmin;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateUser;
    @Autowired
    private MarvelCharacterRepository repository;
    @Autowired
    private SystemUserRepository userRepository;

    // method just to helps create a user.
    private static final SystemUserAccess ADMIN = SystemUserAccess.builder()
            .name("Tony Stark")
            .username("tony.stark")
            .password("{bcrypt}$2a$10$Gk9byx.Lz5fbobWhRfoxc.LV9i10WXUGTSkeoBk/JYouUX7KwSTnK")
            .authorities("ROLE_ADMIN")
            .build();

    private static final SystemUserAccess USER = SystemUserAccess.builder()
            .name("Peter Parker")
            .username("peter.parker")
            .password("{bcrypt}$2a$10$Gk9byx.Lz5fbobWhRfoxc.LV9i10WXUGTSkeoBk/JYouUX7KwSTnK")
            .authorities("ROLE_USER")
            .build();

    private MarvelCharacter saveData(){
        return repository.save(MarvelCharacterCreator.createValidMarvelCharacter());
    }

    @TestConfiguration
    static class Config {
        @Bean(name = "testRestTemplateRoleAdmin")
        @Lazy
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") long randomPort) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + randomPort)
                    .basicAuthentication("tony.stark", "groot");

            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleUser")
        @Lazy
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") long randomPort) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + randomPort)
                    .basicAuthentication("peter.parker", "groot");

            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("findAllNonPageable {} integration test returned of controller with a all marvel characters inside the database")
    void findAllNonPageable_IntegrationTest_When_Successful() {
        userRepository.save(USER);
        saveData();

        List<MarvelCharacter> characters = testRestTemplateUser.exchange(
                "/characters/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MarvelCharacter>>() {}
        ).getBody();

        Assertions.assertThat(characters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findAll {} integration test returned of controller with a pageable list inside the database")
    void findAll_IntegrationTest_When_Successful() {
        userRepository.save(USER);
        saveData();

        PageableResponse<MarvelCharacter> characters = testRestTemplateUser.exchange(
                "/characters",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageableResponse<MarvelCharacter>>() {}
        ).getBody();

        Assertions.assertThat(characters)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("findById {} integration test returned with a single marvel character found by id inside the database")
    void findById_IntegrationTest_When_Successful() {
        userRepository.save(USER);
        Long characterId = saveData().getId();

        MarvelCharacter character = testRestTemplateUser.getForObject(
                "/characters/{id}",
                MarvelCharacter.class,
                1,
                characterId
        );

        Assertions.assertThat(character)
                .isNotNull();

        Assertions.assertThat(character.getId())
                .isNotNull()
                .isEqualTo(characterId);
    }

    @Test
    @DisplayName("findByName {} integration test returned with a single marvel character found by name inside the database")
    void findByName_IntegrationTest_When_Successful() {
        userRepository.save(USER);
        String characterName = saveData().getName();

        List<MarvelCharacter> characters = testRestTemplateUser.exchange(
                "/characters/find?name=" + characterName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MarvelCharacter>>() {}
        ).getBody();

        Assertions.assertThat(characters)
                .isNotEmpty()
                .isNotNull();

        Assertions.assertThat(characters.get(0).getName()).isEqualTo(characterName);
    }

    @Test
    @DisplayName("findByName {} integration edge test returned when marvel character is not found by name inside the database")
    void findByName_IntegrationTest_When_Anime_Not_Found() {
        userRepository.save(USER);

        List<MarvelCharacter> characters = testRestTemplateUser.exchange(
                "/characters/find?name=notToBeFound",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MarvelCharacter>>() {}
        ).getBody();

        Assertions.assertThat(characters)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save {} integration test if the post method was sent and returned data from database")
    void save_IntegrationTest_When_Successful() {
        MarvelCharacterDTO dto = MarvelCharacterCreator.createValidMarvelCharacterDTO();
        userRepository.save(ADMIN);

        ResponseEntity<MarvelCharacter> entity = testRestTemplateAdmin.postForEntity("/characters/admin/", dto,
                MarvelCharacter.class);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("delete {} integration test if the delete method was sent and returned nothing from database")
    void delete_IntegrationTest_When_Successful() {
        userRepository.save(ADMIN);
        MarvelCharacter character = saveData();

        ResponseEntity<Void> entity = testRestTemplateAdmin.exchange(
                "/characters/admin/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                character.getId());

        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace {} integration test if the put method was sent and returned the same data with replaced " +
            "info from database")
    void put_IntegrationTest_When_Successful() {
        userRepository.save(ADMIN);
        MarvelCharacter character = saveData();

        String newCharacterName = "Captain America";

        character.setName(newCharacterName);

        ResponseEntity<Void> entity = testRestTemplateAdmin.exchange(
                "/characters/admin/",
                HttpMethod.PUT,
                new HttpEntity<>(character),
                Void.class,
                character);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThatCode(entity::getBody).doesNotThrowAnyException();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}