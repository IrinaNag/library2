package telran.library.entities;

import java.time.LocalDate;

import javax.persistence.*;

@Table(name = "records")
@Entity
public class Record {
	@Id
	@GeneratedValue
	long id;
	LocalDate pickDate;
	LocalDate returnDate;
	int delayDays;
	@ManyToOne
	Book book;
	@ManyToOne
	Reader reader;

	public Record() {
	}

	public Record(LocalDate pickDate, Book book, Reader reader) {
		this.pickDate = pickDate;
		this.book = book;
		this.reader = reader;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public int getDelayDays() {
		return delayDays;
	}

	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}

	public long getId() {
		return id;
	}

	public LocalDate getPickDate() {
		return pickDate;
	}

	public Book getBook() {
		return book;
	}

	public Reader getReader() {
		return reader;
	}

}
