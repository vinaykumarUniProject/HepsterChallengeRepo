package com.hepster.library.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hepster.library.model.Book;

@DataJpaTest
public class BookRepositoryTest {
	
	@Autowired
	BookRepository bookRepository;
	
	private List<Book> books = new ArrayList<>();

	@BeforeEach
	public void setup() {
		Arrays.asList(0, 1, 2, 3).stream().forEach(i -> {
			Book b = Book.builder().bookId(UUID.randomUUID()).title("title_" + i).author("author_" + i).active(true)
					.price(BigDecimal.valueOf(10.24 + i)).build();
			books.add(b);
		});
	}

	@Test
	public void testFindAllBooks() {
		bookRepository.save(books.get(0));
		List<Book> booksResponse = bookRepository.findAll();
		Assertions.assertThat(booksResponse.get(0).getTitle().equals(books.get(0).getTitle()));
	}
	
	@Test
	public void testFindActiveBooks() {
		Book b = Book.builder().bookId(UUID.randomUUID()).title("title_").author("author_").active(false)
				.price(BigDecimal.valueOf(10.24)).build();
		bookRepository.save(books.get(0));
		bookRepository.save(b);
		List<Book> activeBooksResponse = bookRepository.findBooksByActiveFlag(true);
		Assertions.assertThat(books.size() == 1);
		Assertions.assertThat(bookRepository.findAll().size() == 2);
		Assertions.assertThat(activeBooksResponse.get(0).getTitle().equals(books.get(0).getTitle()));
	}

}
