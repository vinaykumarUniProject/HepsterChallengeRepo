package com.hepster.library.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hepster.library.dto.BookDTO;
import com.hepster.library.exception.BookExceptionsController;
import com.hepster.library.exception.InvalidInputException;
import com.hepster.library.exception.ResourceNotFoundException;
import com.hepster.library.model.Book;
import com.hepster.library.repository.BookRepository;
import com.hepster.library.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

	@InjectMocks
	private BookServiceImpl bookService;

	@Mock
	private BookRepository bookRepository;

	private List<Book> books = new ArrayList<>();
	private List<BookDTO> bookDtos = new ArrayList<>();

	@BeforeEach
	public void setup() {
		Arrays.asList(0, 1, 2, 3).stream().forEach(i -> {
			Book b = Book.builder().bookId(UUID.randomUUID()).title("title_" + i).author("author_" + i).active(true)
					.price(BigDecimal.valueOf(10.24 + i)).build();
			books.add(b);
		});

		Arrays.asList(0, 1, 2, 3).stream().forEach(i -> {
			BookDTO bDto = BookDTO.builder().bookId(UUID.randomUUID()).title("title_" + i).author("author_" + i).active(true)
					.price((10.24 + i) + "").build();
			bookDtos.add(bDto);
		});
	}

	@Test
	public void testFindActiveBooks() {
		Mockito.when(bookRepository.findBooksByActiveFlag(true)).thenReturn(books);
		List<Book> booksResponse = bookService.getActiveBooks(true);
		Assertions.assertThat(booksResponse.size() == 3);
		Assertions.assertThat(booksResponse.get(0).getTitle().equals(books.get(0).getTitle()));
	}

	@Test
	public void testUpdateBook() {
		Map<String, Object> updateRequest = new HashMap<>();
		updateRequest.put("title", "title222");
		updateRequest.put("active", false);
		Mockito.when(bookRepository.findByBookId(books.get(0).getBookId())).thenReturn(books.get(0));
		Book b = bookService.editBook(books.get(0).getBookId(), updateRequest);
		Assertions.assertThat(b.getTitle().equals(updateRequest.get("title")));
	}

	@Test
	public void testUpdateBookWithUnknownID() {
		Map<String, Object> updateRequest = new HashMap<>();
		updateRequest.put("title", "title222");
		updateRequest.put("active", false);
		Mockito.when(bookRepository.findByBookId(books.get(0).getBookId())).thenThrow(new ResourceNotFoundException(BookExceptionsController.BOOK_NOT_FOUND));
		Exception exception = assertThrows(ResourceNotFoundException.class, ()-> bookService.editBook(books.get(0).getBookId(), updateRequest));
		Assertions.assertThat(exception.getMessage().equals(BookExceptionsController.BOOK_NOT_FOUND));
	}
	
	@Test
	public void testUpdateBookWrongKey() {
		Map<String, Object> updateRequest = new HashMap<>();
		updateRequest.put("title1", "title222");
		updateRequest.put("active", false);
		Mockito.when(bookRepository.findByBookId(books.get(0).getBookId())).thenReturn(books.get(0));
		Exception exception = assertThrows(InvalidInputException.class, ()-> bookService.editBook(books.get(0).getBookId(), updateRequest));
		Assertions.assertThat(exception.getMessage().equals(BookExceptionsController.INVALID_KEY_INPUT_IN_JSON));
	}

}
