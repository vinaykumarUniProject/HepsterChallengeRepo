package com.hepster.library.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hepster.library.dto.BookDTO;
import com.hepster.library.model.Book;

public interface BookService {

	public List<Book> getActiveBooks(boolean active);
	
	public Book getBook(UUID bookId);
	
	public Book insertBook(BookDTO bookDto);
	
	public Book editBook(UUID bookId, Map<String, Object> bookRequest);
}
