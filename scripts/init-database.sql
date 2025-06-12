-- Create database
CREATE DATABASE IF NOT EXISTS exam_cell_admin;
USE exam_cell_admin;

-- Insert sample subjects
INSERT INTO subjects (code, name, semester, department, credits, active) VALUES
('CS601', 'Advanced Algorithms', '6', 'Computer Science', 4, true),
('CS602', 'Database Systems', '6', 'Computer Science', 4, true),
('CS603', 'Software Engineering', '6', 'Computer Science', 3, true),
('CS604', 'Computer Networks', '6', 'Computer Science', 4, true),
('CS605', 'Machine Learning', '6', 'Computer Science', 4, true),
('CS501', 'Operating Systems', '5', 'Computer Science', 4, true),
('CS502', 'Compiler Design', '5', 'Computer Science', 4, true),
('CS401', 'Data Structures', '4', 'Computer Science', 4, true),
('CS402', 'Computer Architecture', '4', 'Computer Science', 3, true),
('CS301', 'Object Oriented Programming', '3', 'Computer Science', 4, true);
