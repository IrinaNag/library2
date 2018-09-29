package telran.library.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReturnBookData {
	int readerId;
	String returnDate; // ISO format
	long isbn;

}
