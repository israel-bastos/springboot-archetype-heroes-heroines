package br.com.israelbastos.springbootarchetype.service;

import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;
import br.com.israelbastos.springbootarchetype.exception.notfound.NotFoundException;
import br.com.israelbastos.springbootarchetype.repository.MarvelCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class MarvelCharacterService {
    private final MarvelCharacterRepository repository;

    public List<MarvelCharacter> findAllNonPageable() {
        return repository.findAll();
    }

    public Page<MarvelCharacter> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public MarvelCharacter findByIdOrThrowNotFoundException(long id) {
        return repository
                .findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<MarvelCharacter> findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public MarvelCharacter save(MarvelCharacterDTO dto) {
        MarvelCharacter marvelCharacter = MarvelCharacter.builder()
                .name(dto.getName())
                .build();

        return repository.save(marvelCharacter);
    }

    public void delete(long id) {
        repository.delete(findByIdOrThrowNotFoundException(id));
    }

    public void put(MarvelCharacterDTO dto) {
        MarvelCharacter savedMarvelCharacter = findByIdOrThrowNotFoundException(dto.getId());

        MarvelCharacter marvelCharacter = MarvelCharacter.builder()
                .id(savedMarvelCharacter.getId())
                .name(dto.getName())
                .build();

        repository.save(marvelCharacter);
    }
}