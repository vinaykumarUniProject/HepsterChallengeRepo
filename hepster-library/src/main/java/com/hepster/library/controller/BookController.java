package com.hepster.library.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hepster.library.dto.BookDTO;
import com.hepster.library.model.Book;
import com.hepster.library.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping
	public ResponseEntity<Book> insertBook(@Valid @RequestBody BookDTO bookDto) {
		Book book = bookService.insertBook(bookDto);
		return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<Book> getBook(@PathVariable UUID bookId){
		Book book = bookService.getBook(bookId);
		return ResponseEntity.ok().body(book);
	}
	
	@GetMapping()
	public ResponseEntity<List<Book>> getBooks(){
		return ResponseEntity.ok().body(bookService.getActiveBooks(true));
	}
	
	@PatchMapping("/{bookId}")
	public ResponseEntity<Book> updateBook(@PathVariable UUID bookId, @RequestBody Map<String, Object> bookRequest){
		Book book = bookService.editBook(bookId, bookRequest);
		return new ResponseEntity<Book>(book, HttpStatus.ACCEPTED);
	}
}
