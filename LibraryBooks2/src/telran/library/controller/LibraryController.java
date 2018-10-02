package telran.library.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.library.dto.AuthorDto;
import telran.library.dto.BookDto;
import telran.library.dto.LibraryApiConstants;
import telran.library.dto.LibraryReturnCode;
import telran.library.dto.PickBookData;
import telran.library.dto.ReaderDto;
import telran.library.dto.RecordDto;
import telran.library.dto.ReturnBookData;
import telran.library.service.ILibrary;

@RestController

public class LibraryController {
	@Autowired
	ILibrary library;

	@PostMapping(value = LibraryApiConstants.ADD_AUTHOR)
	public LibraryReturnCode addAuthor(@RequestBody AuthorDto author) {
		return library.addAuthor(author);
	}

	@PostMapping(value = LibraryApiConstants.ADD_BOOK)
	public LibraryReturnCode addBook(@RequestBody BookDto book) {
		return library.addBook(book);
	}

	@PostMapping(value = LibraryApiConstants.PICK_BOOK)
	public LibraryReturnCode pickBook(@RequestBody PickBookData pickBookData) {
		LocalDate pickDate;
		try {
			pickDate = LocalDate.parse(pickBookData.getPickDate());
		} catch (Exception e) {
			return LibraryReturnCode.WRONG_DATE_FORMAT;
		}
		return library.pickBook(pickBookData.getReaderId(), pickBookData.getIsbn(), pickDate);
	}

	@PostMapping(value = LibraryApiConstants.ADD_READER)
	public LibraryReturnCode addReader(@RequestBody ReaderDto reader) {
		return library.addReader(reader);
	}

	@PutMapping(value = LibraryApiConstants.RETURN_BOOK)
	public LibraryReturnCode returnBook(@RequestBody ReturnBookData returnBookData) {
		LocalDate returnDate;
		try {
			returnDate = LocalDate.parse(returnBookData.getReturnDate());
		} catch (Exception e) {
			return LibraryReturnCode.WRONG_DATE_FORMAT;
		}
		return library.returnBook(returnBookData.getReaderId(), returnBookData.getIsbn(), returnDate);
	}

	@GetMapping(value = LibraryApiConstants.GET_READERS_DELAYING)
	public List<ReaderDto> getReadersDelayingBooks() {
		return library.getReadersDelayingBooks();
	}

	@GetMapping(value = LibraryApiConstants.GET_BOOK_AUTHORS)
	public List<AuthorDto> getBookAuthors(@RequestParam long isbn) {
		return library.getBookAuthors(isbn);
	}

	@GetMapping(value = LibraryApiConstants.GET_AUTHOR_BOOKS)
	public List<BookDto> getAuthorBooks(@RequestParam String authorName) {
		return library.getAuthorBooks(authorName);
	}
	
	@DeleteMapping(value = LibraryApiConstants.DELETE_AUTHOR)
		public List<BookDto> removeAuthor(@RequestParam String authorName){
			return library.removeAuthor(authorName);
	}
	
	@GetMapping("/records")
	public List<RecordDto>getAllRecords(){
		return library.getAllRecords();
	}
	
	@GetMapping("/books")
	public List<BookDto> getAllBooks() {
		return library.getAllBooks();
	}

	

}
