package br.com.israelbastos.springbootarchetype.repository;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarvelCharacterRepository extends JpaRepository<MarvelCharacter, Long> {
    List<MarvelCharacter> findByName(String name);
}