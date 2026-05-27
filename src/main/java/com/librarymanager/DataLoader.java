package com.librarymanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final BookRepository bookRepository;

    public DataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        bookRepository.save(new Book("Java编程思想", "Bruce Eckel", 5));
        bookRepository.save(new Book("Effective Java", "Joshua Bloch", 3));
        bookRepository.save(new Book("Spring Boot实战", "Craig Walls", 2));
        bookRepository.save(new Book("数据库系统概念", "Abraham Silberschatz", 4));
        bookRepository.save(new Book("算法导论", "Thomas H. Cormen", 1));
        bookRepository.save(new Book("设计模式", "Gang of Four", 6));
        bookRepository.save(new Book("Python编程", "Mark Lutz", 3));
        bookRepository.save(new Book("数据结构与算法", "Mark Allen Weiss", 2));
        bookRepository.save(new Book("计算机网络", "Andrew S. Tanenbaum", 4));
        bookRepository.save(new Book("操作系统概念", "Abraham Silberschatz", 3));
        bookRepository.save(new Book("软件工程", "Ian Sommerville", 5));
        bookRepository.save(new Book("机器学习", "Tom Mitchell", 2));
    }
}
