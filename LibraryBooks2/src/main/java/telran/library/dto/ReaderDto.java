package telran.library.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class ReaderDto {
	private int id;
	private String name;
	private int year;
	private long phone;


}
