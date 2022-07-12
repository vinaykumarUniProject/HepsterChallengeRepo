package com.hepster.library.dto;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookDTO {
	
	private UUID bookId;
	
	@NotEmpty(message = "Enter a valid title")
	private String title;
	
	private String author;
	
	@NotEmpty(message = "price should be a valid decimal")
	private String price;
	
	private Boolean active;
}
