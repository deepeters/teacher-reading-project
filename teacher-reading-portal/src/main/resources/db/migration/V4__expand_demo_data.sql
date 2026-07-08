UPDATE books
SET content = $$Lina had always wondered what lay beyond the tall trees behind her grandmother's house. One morning, with a notebook in her hand and courage in her heart, she stepped into the forest. The path was soft with moss, and every branch seemed to point her toward a place she had never been brave enough to visit.

As she walked deeper, Lina noticed tiny clues: a blue feather tucked under a stone, a row of bright berries arranged like an arrow, and a stream that hummed as it moved over smooth rocks. She wrote each discovery in her notebook, even though her hands trembled whenever the forest grew quiet.

At last, Lina reached a clearing filled with silver leaves that shimmered in the sun. In the center stood an old wooden sign that read, "Curiosity opens the path." Lina smiled, because she understood then that courage was not the absence of fear. It was taking one careful step forward, then another, until the unknown became a story she could tell.$$,
    description = 'A short story about curiosity, courage, and discovery in a mysterious forest.'
WHERE title = 'The Curious Forest';

UPDATE books
SET content = $$When the rocket engines roared, Jamal pressed his face against the window. Earth became smaller and smaller until it looked like a blue marble floating in darkness. His checklist shook in his hands, but he read every line aloud just like Captain Rivera had taught him.

The moon rose ahead of them, pale and bright, covered with valleys that looked like folds in a blanket. Jamal helped measure the landing zone while the crew prepared the rover. Every number mattered, and for the first time he understood why patience was as important as bravery in space.

When his boots touched the dusty ground, Jamal did not shout. He stood still and listened to the silence. Then he picked up a small moon rock, placed it safely in a sample box, and looked back at Earth with a promise to bring every lesson home.$$,
    description = 'A simple adventure story about focus, teamwork, and space exploration.'
WHERE title = 'Journey to the Moon';

UPDATE books
SET content = $$Behind the old school hall, Nia found a door that nobody seemed to remember. It opened into a library filled with books that whispered when the wind passed through the shelves. Each whisper sounded like the beginning of a question.

Nia called her friend Mateo, and together they followed a trail of golden page corners through the aisles. The trail led them to a locked cabinet, where a dusty map showed every hidden reading room in the school. The map changed whenever they asked it the right question.

By sunset, Nia and Mateo had solved the cabinet's riddle and returned the map to its place. They promised to protect the lost library, not by keeping it secret forever, but by inviting careful readers who knew that books were doors waiting to be opened.$$,
    description = 'A mystery story about books, friendship, and hidden knowledge.'
WHERE title = 'The Lost Library';

INSERT INTO users (name, email, password, role)
VALUES
    ('Ben Carter', 'ben@example.com', '$2a$10$EQklQ3UHbxnR2eE4jFfAQeUt0OUjZEYbye.8GKs6cS7O3zIXeWLmu', 'STUDENT'),
    ('Grace Kim', 'grace@example.com', '$2a$10$EQklQ3UHbxnR2eE4jFfAQeUt0OUjZEYbye.8GKs6cS7O3zIXeWLmu', 'STUDENT')
ON CONFLICT (email) DO NOTHING;

INSERT INTO books (title, author, description, content)
SELECT
    'River of Stars',
    'Elena Brooks',
    'A reflective story about patience, navigation, and learning from mistakes.',
    $$Mara thought the river looked ordinary during the day, but at night its surface filled with points of light. Her grandfather said the stars were reflections from the sky, yet Mara noticed that some of them moved against the current.

She built a small wooden boat and followed the moving lights past reeds, stones, and sleeping houses. Twice she turned too early and had to paddle back, but each mistake taught her how to read the bends of the river more carefully.

Near dawn, the lights gathered around an old bridge where lost travelers had carved their names into the wood. Mara added a small star beside each name, then rowed home knowing that patience could turn even a wrong turn into a map.$$ 
WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'River of Stars'
);

INSERT INTO books (title, author, description, content)
SELECT
    'The Clockwork Garden',
    'Noah Vale',
    'A short tale about invention, responsibility, and keeping promises.',
    $$Every morning, Theo wound the brass key beneath the garden gate. Gears clicked under the soil, metal flowers opened their petals, and tiny sprinklers spun like dancers. The clockwork garden was his mother's greatest invention, and Theo had promised to care for it.

One afternoon, he forgot the key because he was racing with friends by the canal. By evening, the flowers hung low and the little copper bees had stopped in the lavender patch. Theo wanted to hide his mistake, but the silent garden made the truth feel heavy.

He worked through the night cleaning gears, tightening springs, and writing a better care schedule. When the first flower opened at sunrise, Theo understood that responsibility was not about never failing. It was about returning, repairing, and keeping the promise anyway.$$ 
WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE title = 'The Clockwork Garden'
);

INSERT INTO assignments (
    book_id,
    teacher_id,
    student_id,
    due_date,
    status,
    minutes_read,
    created_at,
    updated_at
)
SELECT
    book.id,
    teacher.id,
    student.id,
    assignment_seed.due_date,
    assignment_seed.status,
    assignment_seed.minutes_read,
    NOW(),
    NOW()
FROM (
    VALUES
        ('The Curious Forest', 'student@example.com', CURRENT_DATE + INTERVAL '7 days', 'IN_PROGRESS', 70),
        ('The Lost Library', 'student@example.com', CURRENT_DATE + INTERVAL '14 days', 'NOT_STARTED', 0),
        ('Journey to the Moon', 'alice@example.com', CURRENT_DATE + INTERVAL '3 days', 'COMPLETED', 105),
        ('River of Stars', 'alice@example.com', CURRENT_DATE + INTERVAL '10 days', 'IN_PROGRESS', 35),
        ('The Clockwork Garden', 'ben@example.com', CURRENT_DATE + INTERVAL '21 days', 'NOT_STARTED', 0),
        ('The Curious Forest', 'grace@example.com', CURRENT_DATE + INTERVAL '5 days', 'COMPLETED', 95)
) AS assignment_seed(book_title, student_email, due_date, status, minutes_read)
JOIN books book ON book.title = assignment_seed.book_title
JOIN users teacher ON teacher.email = 'teacher@example.com'
JOIN users student ON student.email = assignment_seed.student_email
WHERE NOT EXISTS (
    SELECT 1
    FROM assignments existing_assignment
    WHERE existing_assignment.book_id = book.id
      AND existing_assignment.teacher_id = teacher.id
      AND existing_assignment.student_id = student.id
);
