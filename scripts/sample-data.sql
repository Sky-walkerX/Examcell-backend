-- Insert sample students
INSERT INTO students (roll_no, name, email, semester, department, phone_number, address, active, created_at, updated_at) VALUES
('CS001', 'John Doe', 'john.doe@example.com', '6th', 'Computer Science', '9876543210', '123 Main St, City', true, NOW(), NOW()),
('CS002', 'Jane Smith', 'jane.smith@example.com', '6th', 'Computer Science', '9876543211', '456 Oak Ave, City', true, NOW(), NOW()),
('CS003', 'Mike Johnson', 'mike.johnson@example.com', '6th', 'Computer Science', '9876543212', '789 Pine Rd, City', true, NOW(), NOW()),
('CS004', 'Sarah Wilson', 'sarah.wilson@example.com', '6th', 'Computer Science', '9876543213', '321 Elm St, City', true, NOW(), NOW()),
('CS005', 'David Brown', 'david.brown@example.com', '5th', 'Computer Science', '9876543214', '654 Maple Dr, City', true, NOW(), NOW()),
('CS006', 'Emily Davis', 'emily.davis@example.com', '5th', 'Computer Science', '9876543215', '987 Cedar Ln, City', true, NOW(), NOW()),
('CS007', 'Robert Miller', 'robert.miller@example.com', '4th', 'Computer Science', '9876543216', '147 Birch Way, City', true, NOW(), NOW()),
('CS008', 'Lisa Garcia', 'lisa.garcia@example.com', '4th', 'Computer Science', '9876543217', '258 Spruce Ct, City', true, NOW(), NOW());

-- Insert sample marks
INSERT INTO marks (student_id, subject_id, marks, grade, exam_type, academic_year, uploaded_by, created_at, updated_at) VALUES
(1, 1, 85, 'A', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(2, 1, 78, 'B+', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(3, 1, 92, 'A+', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(4, 1, 88, 'A', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(1, 2, 90, 'A+', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(2, 2, 82, 'A', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(3, 2, 76, 'B+', 'FINAL', '2023-24', 'admin', NOW(), NOW()),
(4, 2, 94, 'A+', 'FINAL', '2023-24', 'admin', NOW(), NOW());

-- Insert sample queries
INSERT INTO queries (student_id, subject, faculty, title, description, status, priority, created_at, updated_at) VALUES
(1, 'Mathematics', 'Dr. Smith', 'Grade Discrepancy in Mid-term Exam', 'I believe there is an error in my mid-term exam grading. I answered question 5 correctly but received no marks for it.', 'PENDING', 'HIGH', NOW(), NOW()),
(2, 'Physics', 'Prof. Johnson', 'Missing Assignment Marks', 'My assignment marks for Assignment 3 are not reflected in the portal. I submitted it on time.', 'IN_PROGRESS', 'MEDIUM', NOW(), NOW()),
(3, 'Chemistry', 'Dr. Brown', 'Attendance Discrepancy', 'There seems to be an error in my attendance calculation for the month of December.', 'RESOLVED', 'LOW', NOW(), NOW());

-- Insert sample bonafide requests
INSERT INTO bonafide_requests (student_id, purpose, custom_purpose, additional_info, status, created_at, updated_at) VALUES
(1, 'Scholarship Application', '', 'Required for merit scholarship application at XYZ Foundation', 'PENDING', NOW(), NOW()),
(2, 'Bank Loan', '', 'Education loan application at ABC Bank', 'APPROVED', NOW(), NOW()),
(3, 'Other', 'Internship Application', 'Required for summer internship at Tech Corp', 'REJECTED', NOW(), NOW()),
(4, 'Visa Application', '', 'Required for student visa application', 'PENDING', NOW(), NOW());
