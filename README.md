# HepsterChallenge

## Rest apis to post/retrieve Books

### Tech stack:
 - Spring boot
 - H2 in memory data base
 - Junit library with Mockito framework for unit and integration testing

## All REST APIs:

### Post a Book: This api is a POST request that posts a new book into the DB
 - url : http://localhost:8080/books
 - Request json body: { "bookId": "{{$guid}}", "title":String, "author":String, "price":Big decimal, "active":boolean }
 - Note: For logical purposes the "title" field is assumed to be unique

### Get a Book: This api is a GET request that fetches a book with specific Book UUID from the DB
  - url: http://localhost:8080/books/{bookUUID}

### Get all active Books: This api is a GET request that fetches all books with active flag = true
  - url: http://localhost:8080/books

### Update existing book: This api is a PATCH request that updates some fields.  
  - url: http://localhost:8080/books
