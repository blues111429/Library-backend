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
import org.example.backend.model.FieldValue;
import org.example.backend.service.BookService;
import org.example.backend.util.LogEditor;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    // 推荐缓存：userId -> 推荐+打乱后的书单
    private static final Map<Integer, List<BookListResponse>> userRecommendationsCache = new ConcurrentHashMap<>();

    //用户/管理员
    // 获取图书列表（支持分页参数 page & limit）
    @Override
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {
        List<BookListResponse> bookList = bookMapper.getAllBooks();
        return Result.success(bookList);
    }

    // 强制刷新推荐（前端点击 “刷新推荐” 调用）
    @Override
    public Result<List<BookListResponse>> refreshRecommendations(HttpServletRequest httpRequest) {
        Integer userId = UserTools.getUserIdFromRequest(httpRequest);
        if (userId == null) {
            return Result.error("未登录用户无法刷新推荐");
        }

        final int recLimit = 15;
        // 重新生成第一页的推荐并组装（和 getBookList page=1 的流程一致）
        List<BookListResponse> allBooks = bookMapper.getAllBooks();
        List<BookListResponse> recommendations = SmartRecommendations(userId, 1, recLimit);

        if (recommendations.size() > recLimit) recommendations = recommendations.subList(0, recLimit);

        Set<Integer> recIds = recommendations.stream()
                .filter(r -> r != null && r.getId() != null)
                .map(BookListResponse::getId)
                .collect(Collectors.toSet());

        List<BookListResponse> remaining = allBooks.stream()
                .filter(b -> b != null && b.getId() != null && !recIds.contains(b.getId()))
                .collect(Collectors.toList());
        Collections.shuffle(remaining);

        List<BookListResponse> result = new ArrayList<>();
        result.addAll(recommendations);
        result.addAll(remaining);

        userRecommendationsCache.put(userId, result);
        System.out.println("[刷新推荐] 用户 " + userId + " 重新生成推荐列表");
        return Result.success(result);
    }

    //获取图书详情
    @Override
    public Result<Book> getBookById(Integer id) {
        Book book = bookMapper.findBookByIdWithTags(id);
        System.out.println("获取的图书" + book);
        if(book == null){
            return Result.error("未找到该图书");
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
        Book book = bookMapper.findBookByIdWithTags(request.getId());
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

        Book oldBook = bookMapper.findBookByIdWithTags(request.getId());
        if(oldBook == null) {return Result.error("没有该图书");}

        int rows = bookMapper.editBook(request);
        if(rows <= 0) {return Result.error("编辑失败");}

        List<FieldValue> bookFields = Arrays.asList(
                new FieldValue("书名", oldBook.getTitle(), request.getTitle()),
                new FieldValue("作者", oldBook.getAuthor(), request.getAuthor()),
                new FieldValue("分类ID", oldBook.getCategoryId(), request.getCategoryId()),
                new FieldValue("ISBN", oldBook.getIsbn(), request.getIsbn()),
                new FieldValue("总册数", oldBook.getTotalCopies(), request.getTotalCopies()),
                new FieldValue("可借册数", oldBook.getAvailableCopies(), request.getAvailableCopies()),
                new FieldValue("出版社", oldBook.getPublisher(), request.getPublisher()),
                new FieldValue("出版年份", oldBook.getPublishYear(), request.getPublishYear())
        );
        Map<String, Object> oldValues = bookFields.stream().collect(Collectors.toMap(FieldValue::getField, FieldValue::getOldValue));
        Map<String, Object> newValues = bookFields.stream().collect(Collectors.toMap(FieldValue::getField, FieldValue::getNewValue));
        String log = LogEditor.generateEditLog("编辑图书:", String.valueOf(oldBook.getId()), oldValues, newValues);

        UserTools.adminLog(httpRequest, log);
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

    // 智能推荐（带详细日志输出）
    private List<BookListResponse> SmartRecommendations(Integer userId, int page, int limit) {
        System.out.println("=== 智能推荐开始 ===");
        System.out.println("目标用户ID：" + userId + "，页码：" + page);

        // 第 1 页：使用智能推荐
        if (page == 1) {
            if (userId == null) {
                System.out.println("[警告] 用户未登录，返回随机推荐。");
                return bookMapper.findRandomBooks(limit);
            }

            // 1️⃣ 获取用户历史浏览记录
            List<Integer> userBookIds = browseHistoryMapper.findBookIdsByUser(userId);
            if (userBookIds == null || userBookIds.isEmpty()) {
                System.out.println("[提示] 用户无浏览历史，使用随机推荐。");
                return bookMapper.findRandomBooks(limit);
            }

            System.out.println("用户历史相关书籍：" + userBookIds);

            // 2️⃣ 获取偏好
            //图书分类
            List<Map<String, Object>> rawCategoryPref = bookMapper.countCategoryPreference(userBookIds);
            Map<Integer, Integer> categoryPref = new HashMap<>();
            for (Map<String, Object> row : rawCategoryPref) {
                Integer categoryId = ((Number) row.get("category_id")).intValue();
                Integer count = ((Number) row.get("cnt")).intValue();
                categoryPref.put(categoryId, count);
            }
            //作者
            List<Map<String, Object>> rawAuthorPref = bookMapper.countAuthorPreference(userBookIds);
            Map<String, Integer> authorPref = new HashMap<>();
            for (Map<String, Object> row : rawAuthorPref) {
                String author = (String) row.get("author");
                Integer count = ((Number) row.get("cnt")).intValue();
                authorPref.put(author, count);
            }
            System.out.println("分类偏好：" + categoryPref);
            System.out.println("作者偏好：" + authorPref);

            // 3️⃣ 协同过滤计算
            Map<Integer, Double> cfScore = new HashMap<>();
            List<Integer> allUsers = browseHistoryMapper.findAllUserIds();
            for (Integer otherUser : allUsers) {
                if (otherUser.equals(userId)) continue;

                List<Integer> otherBooks = browseHistoryMapper.findBookIdsByUser(otherUser);
                Set<Integer> intersection = new HashSet<>(userBookIds);
                intersection.retainAll(otherBooks);
                Set<Integer> union = new HashSet<>(userBookIds);
                union.addAll(otherBooks);

                double similarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
                if (similarity > 0.0) {
                    for (Integer b : otherBooks) {
                        if (!userBookIds.contains(b)) {
                            cfScore.merge(b, similarity, Double::sum);
                        }
                    }
                }
            }

            // 4️⃣ 内容推荐（分类+作者）
            Map<Integer, Double> contentScore = new HashMap<>();
            List<BookListResponse> allBooks = bookMapper.getAllBooks();
            for (BookListResponse b : allBooks) {
                if (userBookIds.contains(b.getId())) continue;
                double score = 0.0;
                if (b.getCategoryId() != null)
                    score += 0.6 * categoryPref.getOrDefault(b.getCategoryId(), 0);
                if (b.getAuthor() != null)
                    score += 0.4 * authorPref.getOrDefault(b.getAuthor(), 0);
                if (score > 0)
                    contentScore.put(b.getId(), score);
            }

            // 5️⃣ 综合推荐
            Random random = new Random();
            Map<Integer, Double> finalScore = new HashMap<>();
            Set<Integer> allCandidateIds = new HashSet<>();
            allCandidateIds.addAll(cfScore.keySet());
            allCandidateIds.addAll(contentScore.keySet());

            for (Integer bookId : allCandidateIds) {
                double cf = cfScore.getOrDefault(bookId, 0.0);
                double content = contentScore.getOrDefault(bookId, 0.0);
                double score;
                if (cf > 0 && content > 0)
                    score = 0.7 * cf + 0.3 * content;
                else if (cf > 0)
                    score = 0.8 * cf + 0.2 * content;
                else
                    score = 0.4 * cf + 0.6 * content;
                score *= (0.95 + random.nextDouble() * 0.1);
                finalScore.put(bookId, score);
            }

            // 🔹 打印每本书的综合分数，便于调试
            System.out.println("=== 综合推荐分数列表 ===");
            for (Map.Entry<Integer, Double> entry : finalScore.entrySet()) {
                BookListResponse book = bookMapper.findBookListById(entry.getKey());
                if (book != null) {
                    System.out.printf("书名：%s | CF：%.3f | 内容：%.3f | 最终分数：%.3f%n",
                            book.getTitle(),
                            cfScore.getOrDefault(book.getId(), 0.0),
                            contentScore.getOrDefault(book.getId(), 0.0),
                            entry.getValue());
                }
            }
            System.out.println("========================");

            // 6️⃣ 取Top N推荐
            List<Integer> topIds = finalScore.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .toList();

            List<BookListResponse> recommendations = new ArrayList<>();
            for (Integer id : topIds) {
                BookListResponse book = bookMapper.findBookListById(id);
                if (book != null) recommendations.add(book);
            }

            // 打乱推荐顺序，看起来更自然
            Collections.shuffle(recommendations, new Random(System.currentTimeMillis() / 1000));

            System.out.println("推荐结果：" + recommendations.stream().map(BookListResponse::getTitle).toList());
            System.out.println("=== 智能推荐结束 ===");
            return recommendations;
        }

        // 其他页：返回打乱后的普通书籍
        System.out.println("[普通模式] 页码：" + page + "，返回随机打乱书籍");
        List<BookListResponse> allBooks = bookMapper.getAllBooks();
        Collections.shuffle(allBooks);
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, allBooks.size());
        return allBooks.subList(start, end);
    }
}
