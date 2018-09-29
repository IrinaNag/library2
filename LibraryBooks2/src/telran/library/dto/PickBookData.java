package telran.library.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PickBookData {
	// (readerId, isbn, pickDate (better to use string ISO with parsing on server)
	int readerId;
	String pickDate; // ISO format
	long isbn;


}
