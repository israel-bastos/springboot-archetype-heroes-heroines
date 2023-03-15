package br.com.israelbastos.springbootarchetype.repository;

import br.com.israelbastos.springbootarchetype.domain.SystemUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUserAccess, Long> {
    SystemUserAccess findByUsername(String username);
}