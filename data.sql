-- =========================================================================
-- 1. XÓA SẠCH DỮ LIỆU CŨ TRONG CÁC BẢNG (Dùng CASCADE để tự dọn khóa ngoại)
-- =========================================================================
-- TRUNCATE TABLE notifications CASCADE;
-- TRUNCATE TABLE reviews CASCADE;
-- TRUNCATE TABLE lesson_progress CASCADE;
-- TRUNCATE TABLE enrollments CASCADE;
-- TRUNCATE TABLE lessons CASCADE;
-- TRUNCATE TABLE courses CASCADE;
-- TRUNCATE TABLE users CASCADE;
--
-- -- Reset các vòng đếm ID tự tăng (Sequence) về 1 để DB sạch hoàn toàn
-- ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS courses_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS lessons_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS enrollments_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS lesson_progress_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS reviews_review_id_seq RESTART WITH 1;
-- ALTER SEQUENCE IF EXISTS notifications_id_seq RESTART WITH 1;


-- =========================================================================
-- 2. CHÈN LẠI DỮ LIỆU MỚI (KHÔNG CẦN TRUYỀN ID - ĐỂ DATABASE TỰ TĂNG)
-- Mật khẩu đăng nhập cho mọi tài khoản: '123456'
-- =========================================================================

-- A. Bảng Người dùng (users)
INSERT INTO users (user_name, full_name, email, password_hash, role, is_active, created_at, updated_at) VALUES
                                                                                                            ('admin', 'Quản trị viên Admin', 'admin@edu.com', '$2a$10$R77SclWpMvUatgGvIft7eOuxg8b7Qn7A.jXgV8z0k.0yD9PqW6hK6', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                            ('teacher_a', 'Giảng viên Nguyễn Văn A', 'teacher.a@edu.com', '$2a$10$R77SclWpMvUatgGvIft7eOuxg8b7Qn7A.jXgV8z0k.0yD9PqW6hK6', 'TEACHER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                            ('teacher_b', 'Giảng viên Trần Thị B', 'teacher.b@edu.com', '$2a$10$R77SclWpMvUatgGvIft7eOuxg8b7Qn7A.jXgV8z0k.0yD9PqW6hK6', 'TEACHER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                            ('student_hoc', 'Sinh viên Nguyễn Văn Học', 'student.hoc@edu.com', '$2a$10$R77SclWpMvUatgGvIft7eOuxg8b7Qn7A.jXgV8z0k.0yD9PqW6hK6', 'STUDENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                            ('student_cham', 'Sinh viên Trần Thị Chăm', 'student.cham@edu.com', '$2a$10$R77SclWpMvUatgGvIft7eOuxg8b7Qn7A.jXgV8z0k.0yD9PqW6hK6', 'STUDENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- B. Bảng Khóa học (courses) - Lấy teacher_id tự động dựa vào user_name
INSERT INTO courses (title, description, price, duration_hours, status, teacher_id, created_at, updated_at) VALUES
                                                                                                                ('Lập trình Java Cấu trúc dữ liệu', 'Khóa học Java từ cơ bản đến nâng cao cho người mới', 199000.00, 40, 'PUBLISHED', (SELECT id FROM users WHERE user_name = 'teacher_a'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                ('Thiết kế Web với ReactJS xịn sò', 'Học ReactJS qua các dự án thực tế doanh nghiệp', 299000.00, 32, 'PUBLISHED', (SELECT id FROM users WHERE user_name = 'teacher_a'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                ('Triển khai hạ tầng AWS Cloud', 'Cơ bản về điện toán đám mây Amazon AWS', 450000.00, 26, 'PUBLISHED', (SELECT id FROM users WHERE user_name = 'teacher_b'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                ('Lập trình Python và AI', 'Khóa học đang trong chế độ nháp chưa công khai', 150000.00, 15, 'DRAFT', (SELECT id FROM users WHERE user_name = 'teacher_b'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- C. Bảng Bài học (lessons) - Lấy course_id tự động dựa vào title khóa học
INSERT INTO lessons (course_id, title, text_content, content_url, order_index, is_published, created_at, updated_at) VALUES
                                                                                                                         ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), 'Bài 1: Giới thiệu về Java và Môi trường cài đặt', 'Đây là nội dung bài học đầu tiên về cấu hình JDK và viết chương trình Hello World thần thánh...', 'http://example.com/docs/java1', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), 'Bài 2: Biến và các Kiểu dữ liệu nguyên thủy', 'Học về int, double, float, boolean và cách khai báo biến trong Java chuẩn clean code...', 'http://example.com/docs/java2', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), 'Bài 3: Cấu trúc điều kiện If-Else', 'Nội dung nâng cao về rẽ nhánh luồng dữ liệu logic thuật toán...', 'http://example.com/docs/java3', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ((SELECT id FROM courses WHERE title = 'Thiết kế Web với ReactJS xịn sò'), 'Bài 1: Tổng quan về Single Page Application và React', 'Tìm hiểu lý do tại sao ReactJS lại thống trị thế giới Frontend hiện tại...', 'http://example.com/docs/react1', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ((SELECT id FROM courses WHERE title = 'Thiết kế Web với ReactJS xịn sò'), 'Bài 2: React Component và Props', 'Cách thức chia nhỏ giao diện thành các viên gạch component độc lập...', 'http://example.com/docs/react2', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- D. Bảng Đăng ký khóa học (enrollments) - Lấy sinh viên và khóa học linh động theo tên/tiêu đề
INSERT INTO enrollments (course_id, student_id, status, progress_percentage, enrollment_date, completion_date) VALUES
                                                                                                                   ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), (SELECT id FROM users WHERE user_name = 'student_hoc'), 'ENROLLED', 33.33, CURRENT_TIMESTAMP, NULL),
                                                                                                                   ((SELECT id FROM courses WHERE title = 'Thiết kế Web với ReactJS xịn sò'), (SELECT id FROM users WHERE user_name = 'student_hoc'), 'ENROLLED', 0.00, CURRENT_TIMESTAMP, NULL),
                                                                                                                   ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), (SELECT id FROM users WHERE user_name = 'student_cham'), 'ENROLLED', 66.66, CURRENT_TIMESTAMP, NULL),
                                                                                                                   ((SELECT id FROM courses WHERE title = 'Triển khai hạ tầng AWS Cloud'), (SELECT id FROM users WHERE user_name = 'student_cham'), 'COMPLETED', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- E. Bảng Tiến độ bài học (lesson_progress)
INSERT INTO lesson_progress (enrollment_id, lesson_id, is_completed, completed_at, last_accessed_at) VALUES
                                                                                                         (
                                                                                                             (SELECT id FROM enrollments WHERE student_id = (SELECT id FROM users WHERE user_name = 'student_hoc') AND course_id = (SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu')),
                                                                                                             (SELECT id FROM lessons WHERE title = 'Bài 1: Giới thiệu về Java và Môi trường cài đặt'),
                                                                                                             true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                                                                                                         ),
                                                                                                         (
                                                                                                             (SELECT id FROM enrollments WHERE student_id = (SELECT id FROM users WHERE user_name = 'student_cham') AND course_id = (SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu')),
                                                                                                             (SELECT id FROM lessons WHERE title = 'Bài 1: Giới thiệu về Java và Môi trường cài đặt'),
                                                                                                             true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                                                                                                         ),
                                                                                                         (
                                                                                                             (SELECT id FROM enrollments WHERE student_id = (SELECT id FROM users WHERE user_name = 'student_cham') AND course_id = (SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu')),
                                                                                                             (SELECT id FROM lessons WHERE title = 'Bài 2: Biến và các Kiểu dữ liệu nguyên thủy'),
                                                                                                             true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                                                                                                         );


-- F. Bảng Đánh giá / Bình luận (reviews)
INSERT INTO reviews (course_id, student_id, rating, comment, created_at, updated_at) VALUES
                                                                                         ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), (SELECT id FROM users WHERE user_name = 'student_hoc'), 5, 'Khóa học dạy rất chi tiết, giảng viên hỗ trợ nhiệt tình!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                         ((SELECT id FROM courses WHERE title = 'Lập trình Java Cấu trúc dữ liệu'), (SELECT id FROM users WHERE user_name = 'student_cham'), 4, 'Nội dung rất hay nhưng phần bài tập thực hành hơi khó một chút.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                         ((SELECT id FROM courses WHERE title = 'Triển khai hạ tầng AWS Cloud'), (SELECT id FROM users WHERE user_name = 'student_cham'), 5, 'Quá tuyệt vời, các bài Lab thực hành rất thực tế bám sát dự án.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- G. Bảng Thông báo (notifications)
INSERT INTO notifications (user_id, message, type, target_url, is_read, created_at) VALUES
                                                                                        ((SELECT id FROM users WHERE user_name = 'student_hoc'), 'Chào mừng bạn đã tham gia khóa học Java Cấu trúc dữ liệu.', 'ENROLLMENT_CONFIRMED', '/courses/101', false, CURRENT_TIMESTAMP),
                                                                                        ((SELECT id FROM users WHERE user_name = 'student_cham'), 'Chúc mừng bạn đã xuất sắc hoàn thành khóa học AWS Cloud!', 'LESSON_UPDATED', '/courses/103', true, CURRENT_TIMESTAMP);


select * from users;

UPDATE users
SET role = 'ADMIN', is_active = true
WHERE email = 'mtp@gmail.com';

UPDATE users
SET password_hash = '$2a$10$X5pY67Z3WzH9Q7X.u5U7yeBvG3.9jW7lF9yMvX6e7x8k9l0m1n2o3';

UPDATE users
SET is_active = true, role = 'TEACHER'
WHERE email = 'teacher.pro@edu.com';

UPDATE users
SET is_active = true, role = 'ADMIN'
WHERE email = 'admin.pro@edu.com';

UPDATE users
SET is_active = true, role = 'TEACHER'
where email='ngocha@edu.com';


select * from users;

INSERT INTO notifications (user_id, message, type, target_url, is_read, created_at)
VALUES (
           9,
           'Chào mừng Lê Hoàng Việt tham gia khóa học mới!',
           'NEW_COURSE', -- 🌟 Đã sửa thành giá trị hợp lệ trong Enum của bạn
           '/courses',
           false,
           NOW()
       );

