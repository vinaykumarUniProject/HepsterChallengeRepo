package com.hepster.library.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BookExceptionsController {
	
	public static final String BOOK_NOT_FOUND = "Book with given ID not found";
	
	public static final String INVALID_NUMBER_INPUT = "Invalid number input";
	
	public static final String INVALID_KEY_INPUT_IN_JSON = "Invalid Key input in Json";
	
	public static final String TITLE_ALREADY_PRESENT = "Book with the same title/ID already present";
	
	@ExceptionHandler(value = ResourceNotFoundException.class)
	public ResponseEntity<Object> handleException(ResourceNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = NumberFormatException.class)
	public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex){
		return new ResponseEntity<>(BookExceptionsController.INVALID_NUMBER_INPUT,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidInputException.class)
	public ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
		return new ResponseEntity<>(BookExceptionsController.TITLE_ALREADY_PRESENT,HttpStatus.BAD_REQUEST);
	}
	
}
