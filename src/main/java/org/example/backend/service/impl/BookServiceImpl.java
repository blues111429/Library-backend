package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.dto.response.book.BookShelf;
import org.example.backend.dto.response.book.BorrowRecordResponse;
import org.example.backend.mapper.BookMapper;
import org.example.backend.mapper.BorrowRecordMapper;
import org.example.backend.mapper.BrowseHistoryMapper;
import org.example.backend.model.Book;
import org.example.backend.model.BorrowRecord;
import org.example.backend.model.BrowseHistory;
import org.example.backend.service.BookService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BrowseHistoryMapper browseHistoryMapper;

    public BookServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper, BrowseHistoryMapper browseHistoryMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.browseHistoryMapper = browseHistoryMapper;
    }

    //用户/管理员
    //获取图书列表
    @Override
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {
        List<BookListResponse> books = bookMapper.getAllBooks();
        UserTools.adminLog(httpRequest, "获取图书列表");
        return Result.success(books);
    }
    //获取图书详情
    @Override
    public Result<Book> getBookById(Integer id) {
        Book book = bookMapper.findBookById(id);
        if(book == null){
            return Result.error("为找到该图书");
        }
        bookMapper.incrementViewCount(id);
        return Result.success(book);
    }

    //管理员
    //修改图书状态
    @Override
    public Result<String> updateStatus(UpdateStatusRequest request, HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        //查询图书
        Book book = bookMapper.findBookById(request.getId());
        //状态修改
        if(bookMapper.updateStatus(request.getId(), request.getStatus()) <= 0) {
            return Result.error("修改失败");
        }
        UserTools.adminLog(httpRequest, "修改图书状态, 图书ID:"+book.getId()+", 状态由"+book.getStatusText()+"修改至"+(request.getStatus() == 1 ? "上架" : "下架"));
        return Result.success("修改成功");
    }

    //编辑图书
    @Override
    public Result<String> editBook(EditBookRequest request, HttpServletRequest httpRequest) {
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        Book oldBook = bookMapper.findBookById(request.getId());
        if(oldBook == null) {return Result.error("没有该图书");}

        int rows = bookMapper.editBook(request);
        if(rows <= 0) {return Result.error("编辑失败");}

        // 4️⃣ 生成修改日志
        StringBuilder logBuilder = new StringBuilder("编辑图书ID: " + oldBook.getId() + " 修改字段：");

        if (request.getTitle() != null && !request.getTitle().equals(oldBook.getTitle())) {
            logBuilder.append("书名从[").append(oldBook.getTitle()).append("]改为[").append(request.getTitle()).append("]；");
        }
        if (request.getAuthor() != null && !request.getAuthor().equals(oldBook.getAuthor())) {
            logBuilder.append("作者从[").append(oldBook.getAuthor()).append("]改为[").append(request.getAuthor()).append("]；");
        }
        if (request.getCategoryId() != null && !request.getCategoryId().equals(oldBook.getCategoryId())) {
            logBuilder.append("分类ID从[").append(oldBook.getCategoryId()).append("]改为[").append(request.getCategoryId()).append("]；");
        }
        if (request.getIsbn() != null && !request.getIsbn().equals(oldBook.getIsbn())) {
            logBuilder.append("ISBN从[").append(oldBook.getIsbn()).append("]改为[").append(request.getIsbn()).append("]；");
        }
        if (request.getTotalCopies() != null && !request.getTotalCopies().equals(oldBook.getTotalCopies())) {
            logBuilder.append("总册数从[").append(oldBook.getTotalCopies()).append("]改为[").append(request.getTotalCopies()).append("]；");
        }
        if (request.getAvailableCopies() != null && !request.getAvailableCopies().equals(oldBook.getAvailableCopies())) {
            logBuilder.append("可借册数从[").append(oldBook.getAvailableCopies()).append("]改为[").append(request.getAvailableCopies()).append("]；");
        }
        if (request.getPublisher() != null && !request.getPublisher().equals(oldBook.getPublisher())) {
            logBuilder.append("出版社从[").append(oldBook.getPublisher()).append("]改为[").append(request.getPublisher()).append("]；");
        }
        if (request.getPublishYear() != null && !request.getPublishYear().equals(oldBook.getPublishYear())) {
            logBuilder.append("出版年份从[").append(oldBook.getPublishYear()).append("]改为[").append(request.getPublishYear()).append("]；");
        }
        UserTools.adminLog(httpRequest, logBuilder.toString());
        return Result.success("编辑成功");
    }
    //新增图书
    @Override
    public Result<String> addBook(AddBookRequest request, HttpServletRequest httpRequest) {
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        // 重复检测：ISBN 或 书名
        if (request.getIsbn() != null && bookMapper.findBookByIsbn(request.getIsbn()) != null) {return Result.error("该 ISBN 对应的书已存在");}
        if (bookMapper.findBookByTitle(request.getTitle()) != null) {return Result.error("该书名已存在");}

        // 插入新书
        int rows = bookMapper.addBook(request);
        if(rows <= 0) {return Result.error("新增失败");}

        // 管理员操作日志
        String action = String.format(
                "管理员新增图书：[%s] 作者：%s 出版社：%s ISBN：%s",
                request.getTitle(), request.getAuthor(), request.getPublisher(), request.getIsbn()
        );
        UserTools.adminLog(httpRequest, action);

        return Result.success("新增成功");
    }

    //借阅图书
    @Override
    public Result<String> borrowBook(BorrowBookRequest request, HttpServletRequest httpRequest) {
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        Book book = bookMapper.findBookById(request.getBookId());
        if(book == null) return Result.error("图书不存在");
        if(book.getAvailableCopies() <= 0) return Result.error("库存不足");

        if(borrowRecordMapper.countActiveBorrow(UserTools.getUserIdFromRequest(httpRequest), request.getBookId()) > 0) {
            return Result.error("该图书已在您的书架中,无法重复借阅");
        }

        //扣库存
        if(bookMapper.updateAvailableCopies(book.getId(), -1) <=0 ) {
            return Result.error("库存扣除失败,请重试");
        }

        BorrowRecord record = BorrowRecord.builder()
                .userId(UserTools.getUserIdFromRequest(httpRequest))
                .bookId(book.getId())
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .status("borrowed")
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        if(borrowRecordMapper.insert(record) <= 0) {
            return Result.error("借阅失败");
        }
        return Result.success("借阅成功");
    }

    //归还图书
    @Override
    public Result<String> returnBook(ReturnBookRequest request, HttpServletRequest httpRequest) {
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        int id = request.getId();
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(id);

        if(borrowRecord == null) {return Result.error("借阅记录不存在");}
        if(!borrowRecord.getStatus().equals("borrowed")) {return Result.error("图书已归还");}
        if(borrowRecordMapper.updateReturned(id, LocalDateTime.now()) <= 0) {return Result.error("归还失败");}
        if(bookMapper.updateAvailableCopies(borrowRecord.getBookId(), 1) <=0 ) {return Result.error("图书可借数量修改失败");}
        return Result.success("归还成功");
    }

    //借阅列表
    @Override
    public Result<List<BorrowRecordResponse>> borrowList(HttpServletRequest httpRequest) {
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        List<BorrowRecordResponse> records = borrowRecordMapper.selectAll(UserTools.getUserIdFromRequest(httpRequest));
        return Result.success(records);
    }

    //书架列表
    @Override
    public Result<List<BookShelf>> getBookShelf(HttpServletRequest httpRequest) {
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        List<BookShelf> books = bookMapper.selectBooksByUserId(UserTools.getUserIdFromRequest(httpRequest));
        return Result.success(books);
    }

    @Override
    public Result<String> addHistory(BrowseHistoryRequest request, HttpServletRequest httpRequest) {
        BrowseHistory history = BrowseHistory.builder()
                .userId(UserTools.getUserIdFromRequest(httpRequest))
                .bookId(request.getBookId())
                .browseDate(LocalDateTime.now())
                .build();
        browseHistoryMapper.insert(history);
        return Result.success("浏览记录插入成功");
    }
}
