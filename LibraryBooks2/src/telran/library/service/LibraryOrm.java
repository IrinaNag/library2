package telran.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.library.dao.AuthorsRepository;
import telran.library.dao.BooksRepository;
import telran.library.dao.ReadersRepository;
import telran.library.dao.RecordsRepository;
import telran.library.dto.AuthorDto;
import telran.library.dto.BookDto;
import telran.library.dto.LibraryReturnCode;
import telran.library.dto.ReaderDto;
import telran.library.dto.RecordDto;
import telran.library.entities.Author;
import telran.library.entities.Book;
import telran.library.entities.Reader;
import telran.library.entities.Record;

@Service
public class LibraryOrm implements ILibrary {
	@Autowired
	RecordsRepository recordsRepository;
	@Autowired
	BooksRepository booksRepository;
	@Autowired
	ReadersRepository readersRepository;
	@Autowired
	AuthorsRepository authorsRepository;

	@Override
	@Transactional
	public LibraryReturnCode addAuthor(AuthorDto author) {
		if (authorsRepository.existsById(author.getName()))
			return LibraryReturnCode.AUTHOR_ALREADY_EXISTS;
		authorsRepository.save(new Author(author.getName(), author.getCountry()));
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode addBook(BookDto book) {
		if (booksRepository.existsById(book.getIsbn()))
			return LibraryReturnCode.BOOK_ALREADY_EXISTS;
		List<Author> authors = new ArrayList<>();
		for (String authorName : book.getAuthorNames()) {
			Author author = authorsRepository.findById(authorName).orElse(null);
			if (author == null)
				return LibraryReturnCode.NO_AUTHOR;
			authors.add(author);
		}
		Book bookForSave = new Book(book.getIsbn(), book.getAmount(), book.getTitle(), book.getCover(),
				book.getPickPeriod(), authors);
		booksRepository.save(bookForSave);
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode pickBook(int readerId, long isbn, LocalDate pickDate) {
		Reader reader = readersRepository.findById(readerId).orElse(null);
		if (reader == null)
			return LibraryReturnCode.NO_READER;
		Book book = booksRepository.findById(isbn).orElse(null);
		if (book == null)
			return LibraryReturnCode.NO_BOOK;
		recordsRepository.save(new Record(pickDate, book, reader));
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode addReader(ReaderDto reader) {
		if (readersRepository.existsById(reader.getId()))
			return LibraryReturnCode.READER_ALREADY_EXISTS;
		readersRepository.save(new Reader(reader.getId(), reader.getName(), reader.getYear(), reader.getPhone()));
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode returnBook(int readerId, long isbn, LocalDate returnDate) {
		Reader reader = readersRepository.findById(readerId).orElse(null);
		if (reader == null)
			return LibraryReturnCode.NO_READER;
		Book book = booksRepository.findById(isbn).orElse(null);
		if (book == null)
			return LibraryReturnCode.NO_BOOK;
		Record record = recordsRepository.findByBookAndReaderAndReturnDateNull(book, reader);
		LocalDate mustReturnDate = record.getPickDate().plusDays(book.getPickPeriod());
		ChronoUnit chronoUnit = ChronoUnit.DAYS;
		int delayDays = (int) (returnDate.isAfter(mustReturnDate) ? chronoUnit.between(mustReturnDate, returnDate) : 0);
		record.setReturnDate(returnDate);
		record.setDelayDays(delayDays);
		return LibraryReturnCode.OK;
	}

	@Override
	public List<ReaderDto> getReadersDelayingBooks() {
		List<ReaderDto> readers = new ArrayList<>();
		for (Reader reader : readersRepository.findAll()) {
			for (Record record : reader.getRecords()) {
				if(record.getReturnDate()==null) {
					readers.add(mapFromReaderToReaderDto(reader));
					break;
				}
			}
		}
		return readers;
	}

	private ReaderDto mapFromReaderToReaderDto(Reader reader) {
		return new ReaderDto(reader.getId(), reader.getName(), reader.getYear(), reader.getPhone());
	}

	@Override
	public List<AuthorDto> getBookAuthors(long isbn) {
		Book book = booksRepository.findById(isbn).orElse(null);
		if (book == null)
			return null;
		List<AuthorDto> authors = new ArrayList<>();
		for (Author author : book.getAuthors()) {
			authors.add(new AuthorDto(author.getName(), author.getCountry()));
		}
		return authors;
	}

	@Override
	public List<BookDto> getAuthorBooks(String authorName) {
		Author author = authorsRepository.findById(authorName).orElse(null);
		if (author == null)
			return null;
		List<BookDto> books = new ArrayList<>();
		for (Book book : author.getBooks()) {
			books.add(mapFromBookToBookDto(book));
		}
		return books;
	}

	private BookDto mapFromBookToBookDto(Book book) {
		List<String> authorNames = new ArrayList<>();
		for (Author author : book.getAuthors()) {
			authorNames.add(author.getName());
		}
		return new BookDto(book.getIsbn(), book.getTitle(), book.getAmount(), authorNames, book.getCover(),
				book.getPickPeriod());
	}

	@Override
	public List<BookDto> getMostPopularBooks(int yearFrom, int yearTo) {
		// Not Implemented
		return null;
	}

	@Override
	public List<ReaderDto> getMostActiveReaders() {
		// Not Implemented
		return null;
	}

	@Override
	@Transactional
	public List<BookDto> removeAuthor(String authorName) {
		Author author=authorsRepository.findById(authorName).orElse(null);
		if(author==null)
			return null;
		List<BookDto>books=new ArrayList<>();
		for (Book book : author.getBooks()) {
			for (Record record : book.getRecords()) {
				recordsRepository.delete(record);
			}
			booksRepository.delete(book);
			books.add(mapFromBookToBookDto(book));
		}
		return books;
	}

	@Override
	public List<RecordDto> getAllRecords() {
		List<RecordDto>records=new ArrayList<>();
		for (Record record : recordsRepository.findAll()) {
			records.add(new RecordDto(record.getPickDate(), record.getReturnDate()					, record.getDelayDays()
					, record.getBook().getIsbn(), record.getReader().getId()));
		}
		return records;
	}

	@Override
	public List<BookDto> getAllBooks() {
		List<BookDto>books=new ArrayList<>();
		for (Book book : booksRepository.findAll()) {
			books.add(mapFromBookToBookDto(book));
		}
		return books;
	}

}
