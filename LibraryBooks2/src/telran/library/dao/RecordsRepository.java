package telran.library.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.library.entities.Book;
import telran.library.entities.Reader;
import telran.library.entities.Record;

public interface RecordsRepository extends JpaRepository<Record, Long>{

	Record findByBookAndReaderAndReturnDateNull(Book book, Reader reader);

	int countByBookAndReturnDateNull(Book book);
	
	@Query(value="select count(*) from records group by reader_id "
			+"order by count(*) desc limit 1",nativeQuery=true)
	long getMaxRecords();
	
	@Query("select reader.id from Record group by reader.id having count(*)=:count")
	List<Integer>getMostActiveReaders(@Param("count")long count);
	
	@Query(value="select count(*) from records join readers on reader_id=readers.id "
			+"where year between :min and :max group by book_isbn "
			+ "order by count(*) desc limit 1", nativeQuery=true)
	long getMaxRecords(@Param("min")int min, @Param("max")int max);
	
	@Query("select book.isbn from Record "
			+"where reader.year between :min and :max group by book_isbn "
			+"having count(*)=:count")
	List<Long>getMostPopularBooks(@Param("min")int min, @Param("max")int max, @Param("count")long count);

}
