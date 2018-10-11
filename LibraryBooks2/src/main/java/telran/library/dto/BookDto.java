package telran.library.dto;

import java.util.*;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class BookDto {
	long isbn;
	String title;
	int amount;
	List<String> authorNames;
	Cover cover;
	int pickPeriod;


}
