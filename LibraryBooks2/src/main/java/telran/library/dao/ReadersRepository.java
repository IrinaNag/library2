package telran.library.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.library.entities.Reader;

public interface ReadersRepository extends JpaRepository<Reader, Integer> {

}
