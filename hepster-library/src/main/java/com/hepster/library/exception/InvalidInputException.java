package com.hepster.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidInputException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidInputException(String message) {
		super(message);
	}

}
