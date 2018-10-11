package telran.library.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.library.entities.Author;

public interface AuthorsRepository extends JpaRepository<Author, String> {

}
