package evolution.repository;

import evolution.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // 查找用户当前未还的借阅记录
    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    // 查找用户逾期的借阅记录
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.returnDate IS NULL AND br.dueDate < :now")
    List<BorrowRecord> findOverdueByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
