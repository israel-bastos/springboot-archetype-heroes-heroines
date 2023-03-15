package br.com.israelbastos.springbootarchetype.controller;
import br.com.israelbastos.springbootarchetype.domain.MarvelCharacter;
import br.com.israelbastos.springbootarchetype.domain.dto.MarvelCharacterDTO;
import br.com.israelbastos.springbootarchetype.service.MarvelCharacterService;
import br.com.israelbastos.springbootarchetype.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("characters")
@Log4j2
@RequiredArgsConstructor
public class MarvelCharacterController {
    private final DateUtil dateUtil;
    private final MarvelCharacterService service;

    @GetMapping
    public ResponseEntity<Page<MarvelCharacter>> findAll(Pageable pageable) {
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));

        return ResponseEntity.ok(service.findAll(pageable));
    }
    @GetMapping(path = "/all")
    public ResponseEntity<List<MarvelCharacter>> findAllNonPageable() {
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));

        return ResponseEntity.ok(service.findAllNonPageable());
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<MarvelCharacter> findById(@PathVariable long id) {
        return ResponseEntity.ok(service.findByIdOrThrowNotFoundException(id));
    }
    @GetMapping(path = "/find")
    public ResponseEntity<List<MarvelCharacter>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }
    @PostMapping(path = "/admin/")
    public ResponseEntity<MarvelCharacter> save(@RequestBody @Valid MarvelCharacterDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }
    @DeleteMapping(path = "/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/admin/")
    public ResponseEntity<Void> put(@RequestBody MarvelCharacterDTO dto) {
        service.put(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}