package telran.library.entities;

import java.util.List;

import javax.persistence.*;

@Table(name = "readers")
@Entity
public class Reader {
	@Id
	int id;
	String name;
	int year;
	long phone;
	@OneToMany(mappedBy = "reader")
	List<Record> records;

	public Reader() {
	}

	public Reader(int id, String name, int year, long phone) {
		this.id = id;
		this.name = name;
		this.year = year;
		this.phone = phone;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getYear() {
		return year;
	}

	public List<Record> getRecords() {
		return records;
	}

}
