package evolution.service;

import evolution.bean.Book;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final ConcurrentHashMap<String, Book> store = new ConcurrentHashMap<>();

    public Book create(Book b) {
        if (b == null || b.getIsbn() == null || b.getIsbn().trim().isEmpty())
            throw new IllegalArgumentException("ISBN required");
        String isbn = b.getIsbn().trim();
        if (store.containsKey(isbn)) throw new IllegalArgumentException("ISBN exists");
        store.put(isbn, new Book(isbn, b.getTitle(), b.getAuthor()));
        return store.get(isbn);
    }

    public Book get(String isbn) {
        Book b = store.get(isbn);
        return (b == null || b.isDeleted()) ? null : b;
    }

    public Book update(String isbn, Book update) {
        Book b = store.get(isbn);
        if (b == null || b.isDeleted()) throw new IllegalArgumentException("Book not found");
        if (update.getTitle() != null) b.setTitle(update.getTitle());
        if (update.getAuthor() != null) b.setAuthor(update.getAuthor());
        return b;
    }

    public boolean delete(String isbn, boolean confirm) {
        if (!confirm) return false;
        return store.remove(isbn) != null;
    }

    public List<Book> search(String title, String author, String isbn) {
        return store.values().stream()
                .filter(x -> !x.isDeleted())
                .filter(x -> isbn == null || isbn.isEmpty() || x.getIsbn().equalsIgnoreCase(isbn))
                .filter(x -> title == null || title.isEmpty() || x.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(x -> author == null || author.isEmpty() || x.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> listAll() {
        return store.values().stream().filter(b -> !b.isDeleted()).collect(Collectors.toList());
    }
}