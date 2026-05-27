package evolution.librarymanager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final BookRepository bookRepository;

    public SearchController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/")
    public String home() {
        return "search";
    }

    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books;

        if (keyword == null || keyword.trim().isEmpty()) {
            books = bookRepository.findAll(pageable);
        } else {
            books = bookRepository.searchBooks(keyword.trim(), pageable);
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());

        return "search";
    }
}
