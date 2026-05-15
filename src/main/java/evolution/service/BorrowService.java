package evolution.service;

import evolution.controller.bean.SuccessResponse;
import evolution.entity.Book;
import evolution.entity.BorrowRecord;
import evolution.entity.User;
import evolution.repository.BookRepository;
import evolution.repository.BorrowRecordRepository;
import evolution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    // 实现借书业务逻辑
    // 1. 校验用户是否存在、是否有逾期未还
    // 2. 校验图书是否可借
    // 3. 校验用户当前已借数量是否超过上限（5本）
    // 4. 更新图书状态为已借出
    // 5. 生成借阅记录，记录借书时间和应还日期
    public SuccessResponse borrowBook(Long userId, Long bookId) {
        // 1. 校验用户是否存在
        if (!userRepository.existsById(userId)) {
            return new SuccessResponse(false, "用户不存在");
        }

        // 校验是否有逾期未还
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueByUserId(userId, LocalDateTime.now());
        if (!overdueRecords.isEmpty()) {
            return new SuccessResponse(false, "用户有逾期未还的图书");
        }

        // 2. 校验图书是否可借
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return new SuccessResponse(false, "图书不存在");
        }
        if (!book.isAvailable()) {
            return new SuccessResponse(false, "图书不可借");
        }

        // 3. 校验用户当前已借数量是否超过上限（5本）
        List<BorrowRecord> currentBorrows = borrowRecordRepository.findByUserIdAndReturnDateIsNull(userId);
        if (currentBorrows.size() >= 5) {
            return new SuccessResponse(false, "用户已借图书数量超过上限（5本）");
        }

        // 4. 更新图书状态为已借出
        book.setAvailable(false);
        bookRepository.save(book);

        // 5. 生成借阅记录，记录借书时间和应还日期（假设借期30天）
        User user = userRepository.findById(userId).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(30);
        BorrowRecord record = new BorrowRecord(user, book, now, dueDate);
        borrowRecordRepository.save(record);

        return new SuccessResponse(true, "借书成功");
    }
}