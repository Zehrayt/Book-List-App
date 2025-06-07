package util;

public class Book {

	private String bookName, bookGenre;
	private int publication, bookId, authId;
	private Author author;
	private String status;
	
	
	public Book() {
		// TODO Auto-generated constructor stub
	}
	
	public Book(String bookName, String bookGenre, int publication, Author author) {
        this.bookName = bookName;
        this.bookGenre = bookGenre;
        this.publication = publication;
        this.author = author;
    }

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
	
	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookGenre() {
		return bookGenre;
	}

	public void setBookGenre(String bookGenre) {
		this.bookGenre = bookGenre;
	}

	public int getPublication() {
		return publication;
	}

	public void setPublication(int publication) {
		this.publication = publication;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
	
	public int getAuthId() {
	    return authId;
	}

	public void setAuthId(int authId) {
	    this.authId = authId;
	}
	
	 @Override
	    public String toString() {
	        return "Book [name=" + bookName + ", genre=" + bookGenre + ", publication=" + publication +
	               ", author=" + author.getAutName() + " " + author.getAutSurname() + "]";
	    }

}
