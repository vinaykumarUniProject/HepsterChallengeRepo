package com.hepster.library.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique=true)
	@Type(type = "org.hibernate.type.UUIDCharType")
	private UUID bookId;
	
	@Column(unique = true)
	private String title;
	
	private String author;
	
	@DecimalMin(value="0.0", inclusive = true)
	private BigDecimal price;
	
	private Boolean active;
}
