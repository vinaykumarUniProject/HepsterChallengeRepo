package com.hepster.library.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hepster.library.HepsterLibraryApplication;
import com.hepster.library.dto.BookDTO;
import com.hepster.library.exception.BookExceptionsController;
import com.hepster.library.model.Book;

@SpringBootTest(classes = HepsterLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {

	private TestRestTemplate restTemplate = new TestRestTemplate();

	@LocalServerPort
	private int port;

	@Autowired
	private ObjectMapper mapper;

	private HttpHeaders headers = new HttpHeaders();

	private List<BookDTO> bookDtos = new ArrayList<>();

	@BeforeEach
	public void setup() {
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		Arrays.asList(0, 1, 2, 3).stream().forEach(i -> {
			BookDTO bDto = BookDTO.builder().bookId(UUID.randomUUID()).title("title_" + i).author("author_" + i).active(true)
					.price((10.24 + i) + "").build();
			bookDtos.add(bDto);
		});
	}

	@Sql(statements = "INSERT INTO BOOK (ID,BOOK_ID,TITLE,AUTHOR,PRICE,ACTIVE) VALUES (1,'c0d796b5-efd8-45c0-b334-17a66e21c8ec', 'title_0_1', 'author_0_1', '10.24','true')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM BOOK WHERE BOOK_ID='c0d796b5-efd8-45c0-b334-17a66e21c8ec'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@Test
	public void testGetActiveBooks() throws JSONException, JsonMappingException, JsonProcessingException {
		ResponseEntity<String> response = restTemplate.getForEntity(createURL("/books"), String.class);
		JsonNode root = mapper.readTree(response.getBody());
		Assertions.assertEquals(root.get(0).get("title").asText(), "title_0_1");
	}

	@Sql(statements = "INSERT INTO BOOK (ID,BOOK_ID,TITLE,AUTHOR,PRICE,ACTIVE) VALUES (1,'c0d796b5-efd8-45c0-b334-17a66e21c81c', 'title_0_2', 'author_0_2', '10.24','false')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM BOOK WHERE BOOK_ID='c0d796b5-efd8-45c0-b334-17a66e21c81c'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@Test
	public void testGetBookWithID() throws JSONException, JsonMappingException, JsonProcessingException {
		ResponseEntity<Book> response = restTemplate
				.getForEntity(createURL("/books/c0d796b5-efd8-45c0-b334-17a66e21c81c"), Book.class);
		Assertions.assertEquals(response.getBody().getBookId().toString(), "c0d796b5-efd8-45c0-b334-17a66e21c81c");
		Assertions.assertEquals(response.getBody().getTitle(), "title_0_2");
		Assertions.assertEquals(response.getBody().getActive(), false);
	}

	@Test
	public void testPostBook() throws JSONException, JsonMappingException, JsonProcessingException {
		HttpEntity<BookDTO> entity = new HttpEntity<BookDTO>(bookDtos.get(1), headers);
		ResponseEntity<Book> response = restTemplate.exchange(createURL("/books"), HttpMethod.POST, entity, Book.class);
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		Assertions.assertEquals(response.getBody().getTitle(), bookDtos.get(1).getTitle());
	}

	@Test
	public void testPostBookWithNonDecimalPrice() throws JSONException, JsonMappingException, JsonProcessingException {
		BookDTO bDto = BookDTO.builder().title("title_").author("author_").active(true).price(10.24 + "ee").build();
		HttpEntity<BookDTO> entity = new HttpEntity<BookDTO>(bDto, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURL("/books"), HttpMethod.POST, entity,
				String.class);

		Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		Assertions.assertEquals(response.getBody(), BookExceptionsController.INVALID_NUMBER_INPUT);
	}

	@Test
	public void testPostBookWithDuplicateTitle() throws JSONException, JsonMappingException, JsonProcessingException {
		HttpEntity<BookDTO> entity = new HttpEntity<BookDTO>(bookDtos.get(1), headers);
		restTemplate.exchange(createURL("/books"), HttpMethod.POST, entity, String.class);
		ResponseEntity<String> response = restTemplate.exchange(createURL("/books"), HttpMethod.POST, entity,
				String.class);

		Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		Assertions.assertEquals(response.getBody(), BookExceptionsController.TITLE_ALREADY_PRESENT);
	}
	
	@Sql(statements = "INSERT INTO BOOK (ID,BOOK_ID,TITLE,AUTHOR,PRICE,ACTIVE) VALUES (1,'c0d796b5-efd8-45c0-b334-17a66e21c8cc', 'title_11', 'author_11', '10.24','false')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM BOOK WHERE BOOK_ID='c0d796b5-efd8-45c0-b334-17a66e21c8cc'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)	
	@Test
	public void testUpdateBook() throws JSONException, JsonMappingException, JsonProcessingException {
		String updateRequest = "{\"title\": \"title222\",\"active\": "+true+"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(updateRequest, headers);
		ResponseEntity<Book> response = restTemplate.exchange(URI.create(createURL("/books/c0d796b5-efd8-45c0-b334-17a66e21c8cc")) ,
				HttpMethod.PATCH, entity, Book.class);
		Assertions.assertEquals(response.getBody().getTitle(), "title222");
	}
	
	
	@Sql(statements = "INSERT INTO BOOK (ID,BOOK_ID,TITLE,AUTHOR,PRICE,ACTIVE) VALUES (1,'c0d796b5-efd8-45c0-b334-17a66e21c8cc', 'title_11', 'author_11', '10.24','false')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM BOOK WHERE BOOK_ID='c0d796b5-efd8-45c0-b334-17a66e21c8cc'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)	
	@Test
	public void testUpdateBookWithWrongKey() throws JSONException, JsonMappingException, JsonProcessingException {
		String updateRequest = "{\"title1\": \"title222\",\"active\": "+true+"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(updateRequest, headers);
		ResponseEntity<String> response = restTemplate.exchange(URI.create(createURL("/books/c0d796b5-efd8-45c0-b334-17a66e21c8cc")) ,
				HttpMethod.PATCH, entity, String.class);
		Assertions.assertEquals(response.getBody(), BookExceptionsController.INVALID_KEY_INPUT_IN_JSON+": title1");
	}
	
	@Sql(statements = "INSERT INTO BOOK (ID,BOOK_ID,TITLE,AUTHOR,PRICE,ACTIVE) VALUES (1,'c0d796b5-efd8-45c0-b334-17a66e21c8cc', 'title_11', 'author_11', '10.24','false')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM BOOK WHERE BOOK_ID='c0d796b5-efd8-45c0-b334-17a66e21c8cc'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)	
	@Test
	public void testUpdateBookWithUnknownBookId() throws JSONException, JsonMappingException, JsonProcessingException {
		String updateRequest = "{\"title\": \"title222\",\"active\": "+true+"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(updateRequest, headers);
		ResponseEntity<String> response = restTemplate.exchange(URI.create(createURL("/books/c0d796b5-efd8-45c0-b334-17a66e21c8ec")) ,
				HttpMethod.PATCH, entity, String.class);
		Assertions.assertEquals(response.getBody(), BookExceptionsController.BOOK_NOT_FOUND+": c0d796b5-efd8-45c0-b334-17a66e21c8ec");
	}

	private String createURL(String uri) {
		return "http://localhost:" + port + uri;
	}

}
