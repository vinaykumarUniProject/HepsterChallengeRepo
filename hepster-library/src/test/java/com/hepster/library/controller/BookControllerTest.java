package com.hepster.library.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hepster.library.dto.BookDTO;
import com.hepster.library.exception.ResourceNotFoundException;
import com.hepster.library.model.Book;
import com.hepster.library.service.BookService;

@WebMvcTest(BookController.class)
public class BookControllerTest {

	@MockBean
	private BookService bookService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

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
	public void testReturnAllActiveBooks() throws Exception {
		Mockito.when(bookService.getActiveBooks(true)).thenReturn(books);
		mockMvc.perform(MockMvcRequestBuilders.get("/books").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.*", hasSize(4)))
				.andExpect(jsonPath("$[2].title").value(books.get(2).getTitle()));
	}

	@Test
	public void testGetBookWithId() throws Exception {
		UUID uuid = UUID.randomUUID();
		Book b = Book.builder().bookId(uuid).title("title_").author("author_").active(true)
				.price(BigDecimal.valueOf(10.24)).build();
		Mockito.when(bookService.getBook(uuid)).thenReturn(b);

		mockMvc.perform(MockMvcRequestBuilders.get("/books/" + uuid).contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.title").value(b.getTitle()))
				.andExpect(jsonPath("$.author").value(b.getAuthor()));
	}

	@Test
	public void testGetBookWithUnknownId() throws Exception {
		UUID uuid = UUID.randomUUID();
		Mockito.when(bookService.getBook(uuid)).thenThrow(ResourceNotFoundException.class);
		Assertions.assertTrue(mockMvc.perform(MockMvcRequestBuilders.get("/books/" + uuid)).andReturn()
				.getResolvedException() instanceof ResourceNotFoundException);
	}

	@Test
	public void testInsertBook() throws Exception {
		Mockito.when(bookService.insertBook(bookDtos.get(0))).thenReturn(books.get(0));
		mockMvc.perform(MockMvcRequestBuilders.post("/books/").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bookDtos.get(0))).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(bookDtos.get(0).getTitle()));
	}
		
}