package evolution.bean;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean deleted;

    public Book() {}

    public Book(String isbn, String title, String author) {
        this.isbn = isbn; this.title = title; this.author = author; this.deleted = false;
    }

    // getters & setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}