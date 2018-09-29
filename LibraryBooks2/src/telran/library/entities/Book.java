package telran.library.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import telran.library.dto.Cover;

@Table(name = "books")
@Entity
public class Book {
	@Id
	long isbn;
	int amount;
	String title;
	@Enumerated(EnumType.STRING)
	Cover cover;
	int pickPeriod;
	@ManyToMany
	List<Author> authors;
	@OneToMany(mappedBy = "book")
	List<Record> records;

	public Book() {
	}

	public Book(long isbn, int amount, String title, Cover cover, int pickPeriod, List<Author> authors) {
		this.isbn = isbn;
		this.amount = amount;
		this.title = title;
		this.cover = cover;
		this.pickPeriod = pickPeriod;
		this.authors = authors;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPickPeriod() {
		return pickPeriod;
	}

	public void setPickPeriod(int pickPeriod) {
		this.pickPeriod = pickPeriod;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public long getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public Cover getCover() {
		return cover;
	}

	public List<Record> getRecords() {
		return records;
	}

}
