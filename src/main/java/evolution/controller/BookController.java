package evolution.controller;

import org.springframework.web.bind.annotation.*;
import evolution.bean.Book;
import evolution.service.BookService;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service = new BookService();

    @PostMapping
    public Book create(@RequestBody Book b) {
        return service.create(b);
    }

    @GetMapping
    public List<Book> search(@RequestParam(required=false) String title,
                             @RequestParam(required=false) String author,
                             @RequestParam(required=false) String isbn) {
        return service.search(title, author, isbn);
    }

    @GetMapping("/{isbn}")
    public Book get(@PathVariable String isbn) {
        Book b = service.get(isbn);
        if (b == null) throw new ResourceNotFoundException("Not found"); // 你可以改成返回 null 或自定义 response
        return b;
    }

    @PutMapping("/{isbn}")
    public Book update(@PathVariable String isbn, @RequestBody Book update) {
        return service.update(isbn, update);
    }

    @DeleteMapping("/{isbn}")
    public void delete(@PathVariable String isbn, @RequestParam(required=false) boolean confirm) {
        if (!confirm) throw new IllegalArgumentException("confirm required");
        service.delete(isbn, true);
    }
}