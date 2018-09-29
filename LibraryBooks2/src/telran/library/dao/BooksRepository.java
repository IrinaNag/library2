package telran.library.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.library.entities.Book;

public interface BooksRepository extends JpaRepository<Book, Long> {

}
