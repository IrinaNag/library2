package telran.library.dto;

import java.time.LocalDate;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class RecordDto {
	LocalDate pickDate;
	LocalDate returnDate;
	int delayDays;
	long bookIsbn;
	int readerId;

}
