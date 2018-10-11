package telran.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
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

@ManagedResource
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
	@Value("${value.delayPercent:10}")
	int delayPercent;

	@ManagedAttribute
	public int getDelayPercent() {
		return delayPercent;
	}

	@ManagedAttribute
	public void setDelayPercent(int delayPercent) {
		this.delayPercent = delayPercent;
	}

	@Override
	@Transactional
	public LibraryReturnCode addAuthor(AuthorDto author) {
		String authorName = author.getName();
		if (authorsRepository.existsById(authorName))
			return LibraryReturnCode.AUTHOR_ALREADY_EXISTS;
		authorsRepository.save(new Author(authorName, author.getCountry()));
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode addBook(BookDto book) {
		long bookIsbn = book.getIsbn();
		if (booksRepository.existsById(bookIsbn))
			return LibraryReturnCode.BOOK_ALREADY_EXISTS;
		List<Author> authors = new ArrayList<>();
		for (String authorName : book.getAuthorNames()) {
			Author author = authorsRepository.findById(authorName).orElse(null);
			if (author == null)
				return LibraryReturnCode.NO_AUTHOR;
			authors.add(author);
		}
		Book bookForSave = new Book(bookIsbn, book.getAmount(), book.getTitle(), book.getCover(), book.getPickPeriod(),
				authors);
		booksRepository.save(bookForSave);
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode pickBook(int readerId, long isbn, LocalDate pickDate) {
		Reader reader = readersRepository.findById(readerId).orElse(null);
		if (reader == null)
			return LibraryReturnCode.NO_READER;
		for (Record record : reader.getRecords()) {
			if (record.getReturnDate() == null)
				return LibraryReturnCode.READER_NO_RETURNED_BOOK;
		}
		Book book = booksRepository.findById(isbn).orElse(null);
		if (book == null)
			return LibraryReturnCode.NO_BOOK;
		if (recordsRepository.countByBookAndReturnDateNull(book) == book.getAmount())
			return LibraryReturnCode.ALL_BOOKS_IN_USE;
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
		if (record == null)
			return LibraryReturnCode.NO_RECORD_FOR_RETURN;
		if (returnDate.isBefore(record.getPickDate()))
			return LibraryReturnCode.WRONG_RETURN_DATE;
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
				int pickPeriod=record.getBook().getPickPeriod();
				if (record.getReturnDate() == null
						&& LocalDate.now().isAfter(record.getPickDate().plusDays(pickPeriod+pickPeriod*delayPercent/100))) {
					readers.add(mapFromReaderToReaderDto(reader));
					break;
				}
			}
		}
		readers.forEach(System.out::println);
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
		long maxRecords = recordsRepository.getMaxRecords(yearFrom, yearTo);
		List<Long> mostPopularBooksIsbn = recordsRepository.getMostPopularBooks(yearFrom, yearTo, maxRecords);
		return mostPopularBooksIsbn.stream().map(x -> booksRepository.getOne(x)).map(x -> mapFromBookToBookDto(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<ReaderDto> getMostActiveReaders() {
		long maxRecords = recordsRepository.getMaxRecords();
		List<Integer> mostActiveReaders = recordsRepository.getMostActiveReaders(maxRecords);
		return mostActiveReaders.stream().map(x -> readersRepository.getOne(x)).map(x -> mapFromReaderToReaderDto(x))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<BookDto> removeAuthor(String authorName) {
		Author author = authorsRepository.findById(authorName).orElse(null);
		if (author == null)
			return null;
		List<BookDto> books = new ArrayList<>();
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
		List<RecordDto> records = new ArrayList<>();
		for (Record record : recordsRepository.findAll()) {
			records.add(new RecordDto(record.getPickDate(), record.getReturnDate(), record.getDelayDays(),
					record.getBook().getIsbn(), record.getReader().getId()));
		}
		return records;
	}

	@Override
	public List<BookDto> getAllBooks() {
		List<BookDto> books = new ArrayList<>();
		for (Book book : booksRepository.findAll()) {
			books.add(mapFromBookToBookDto(book));
		}
		return books;
	}

}
