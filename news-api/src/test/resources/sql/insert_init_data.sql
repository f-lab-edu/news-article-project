DELETE FROM articles;  -- 기존 데이터 삭제 (필요시)
delete from users;

INSERT INTO articles (title, category, sentiment, views, journalist_id, topic, updated_at, likes, dislikes) VALUES
('김연아 금매달', 'SPORTS', 'POSITIVE', 1500, 1, '김연아', '2010-12-15 08:00:00', 1300, 0),
('손흥민 해트트릭', 'SPORTS', 'POSITIVE', 1300, 2, '손흥민', '2023-12-15 08:00:00', 1000, 0),
('인공지능의 위험', 'IT', 'NEGATIVE', 500, 2, '인공지능', '2024-12-15 08:00:00', 500, 0),
('인공지능의 영향', 'IT', 'POSITIVE', 500, 1, '인공지능', '2025-01-15 08:00:00', 500, 0);
