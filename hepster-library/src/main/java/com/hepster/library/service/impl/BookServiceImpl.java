package com.hepster.library.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.hepster.library.dto.BookDTO;
import com.hepster.library.exception.BookExceptionsController;
import com.hepster.library.exception.InvalidInputException;
import com.hepster.library.exception.ResourceNotFoundException;
import com.hepster.library.model.Book;
import com.hepster.library.repository.BookRepository;
import com.hepster.library.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public Book getBook(UUID bookId) {
		Book book = bookRepository.findByBookId(bookId);
		if(book==null) {
			throw new ResourceNotFoundException(BookExceptionsController.BOOK_NOT_FOUND + ": "+bookId);
		}
		return book;
	}

	@Override
	public List<Book> getActiveBooks(boolean active) {
		return bookRepository.findBooksByActiveFlag(active);
	}

	@Override
	public Book insertBook(BookDTO bookDto) {
		Book book = Book.builder()
						.bookId(bookDto.getBookId())
						.title(bookDto.getTitle())
						.author(bookDto.getAuthor())
						.price(new BigDecimal(bookDto.getPrice()))
						.active(bookDto.getActive())
						.build();
		bookRepository.save(book);
		return book;
	}

	@Override
	public Book editBook(UUID bookId, Map<String, Object> bookRequest) {

		Book book = this.getBook(bookId);
		
		bookRequest.forEach((key,value)->{
			Field field = ReflectionUtils.findField(Book.class, key);
			if(field==null) {
				throw new InvalidInputException(BookExceptionsController.INVALID_KEY_INPUT_IN_JSON + ": "+key);
			}
			field.setAccessible(true);
			switch(key) {
			case "price":
				ReflectionUtils.setField(field, book, new BigDecimal(value.toString()));
				break;
			case "bookId":
				ReflectionUtils.setField(field, book, UUID.fromString(value.toString()));
				break;
			default:
				ReflectionUtils.setField(field, book, value);
				break;
			}
		});
		bookRepository.save(book);
		return book;
	}

}
