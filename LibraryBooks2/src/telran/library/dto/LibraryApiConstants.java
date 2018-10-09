package telran.library.dto;

public interface LibraryApiConstants {
	String ADD_BOOK = "/book/add";
	String ADD_AUTHOR = "/author/add";
	String PICK_BOOK = "/book/pick";
	String ADD_READER = "/reader/add";
	String RETURN_BOOK = "/book/return";
	String GET_READERS_DELAYING = "/readers/delaying/get";
	String GET_BOOK_AUTHORS = "/authors/book/get";
	String GET_AUTHOR_BOOKS = "/books/author/get";
	String DELETE_AUTHOR = "/author/delete";
	String GET_MOST_POPULAR_BOOKS="/books/popular/get";
	String GET_MOST_ACTIVE_READERS="/readers/active/get";
	String ISBN = "isbn";
	String AUTHOR_NAME = "author";
}
