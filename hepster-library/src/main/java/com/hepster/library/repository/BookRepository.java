package com.hepster.library.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hepster.library.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
	
	public Book findByBookId(UUID bookId);

	@Query(value = "SELECT * FROM BOOK WHERE active=?1", nativeQuery = true)
	public List<Book> findBooksByActiveFlag(boolean active);
}
