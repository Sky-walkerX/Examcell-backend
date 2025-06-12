package com.examcell.admin.service;

import com.examcell.admin.dto.AuthRequest;
import com.examcell.admin.dto.AuthResponse;
import com.examcell.admin.dto.SignupRequest;
import com.examcell.admin.entity.Student;
import com.examcell.admin.entity.Teacher;
import com.examcell.admin.entity.User;
import com.examcell.admin.exception.DuplicateResourceException;
import com.examcell.admin.exception.ResourceNotFoundException;
import com.examcell.admin.repository.StudentRepository;
import com.examcell.admin.repository.TeacherRepository;
import com.examcell.admin.repository.UserRepository;
import com.examcell.admin.security.JwtService; // --- NEW IMPORT ---
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager; // --- NEW IMPORT ---
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // --- NEW IMPORT ---
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    // --- NEW INJECTIONS ---
    // These will be provided by the beans you create in SecurityConfig
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(true);

        if (request.getRole() == User.Role.STUDENT) {
            Student student = createStudentFromSignup(request);
            student = studentRepository.save(student);
            user.setStudentId(student.getId());
        } else if (request.getRole() == User.Role.TEACHER) {
            Teacher teacher = createTeacherFromSignup(request);
            teacher = teacherRepository.save(teacher);
            user.setTeacherId(teacher.getId());
        }

        user = userRepository.save(user);

        // --- MODIFICATION: GENERATE A REAL JWT TOKEN ON SIGNUP ---
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Password here doesn't matter as it's not used for comparison
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(
                jwtToken, // Replaced "signup-success" with the actual token
                user.getRole(),
                user.getId(),
                user.getStudentId(),
                user.getTeacherId(),
                user.getFullName(),
                user.getEmail(),
                "Account created successfully"
        );
    }

    public AuthResponse login(AuthRequest request) {
        // --- MODIFICATION: DELEGATE AUTHENTICATION TO SPRING SECURITY ---
        // This line will handle user lookup and password comparison.
        // It will throw an AuthenticationException if credentials are bad.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, authentication was successful.
        // Now, fetch the user to get all details for the response.
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after successful authentication. This should not happen."));

        if (!user.getActive()) {
            throw new ResourceNotFoundException("Account is deactivated");
        }

        // --- MODIFICATION: GENERATE A REAL JWT TOKEN ON LOGIN ---
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );
        String jwtToken = jwtService.generateToken(userDetails);


        return new AuthResponse(
                jwtToken, // Replaced "login-success" with the actual token
                user.getRole(),
                user.getId(),
                user.getStudentId(),
                user.getTeacherId(),
                user.getFullName(),
                user.getEmail(),
                "Login successful"
        );
    }

    // --- NO CHANGES NEEDED IN THE HELPER METHODS BELOW ---

    private Student createStudentFromSignup(SignupRequest request) {
        if (studentRepository.existsByRollNo(request.getRollNo())) {
            throw new DuplicateResourceException("Roll number already exists");
        }

        Student student = new Student();
        student.setRollNo(request.getRollNo());
        student.setName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setSemester(request.getSemester());
        student.setDepartment(request.getDepartment());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setAddress(request.getAddress());
        student.setActive(true);
        return student;
    }

    private Teacher createTeacherFromSignup(SignupRequest request) {
        if (teacherRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setDepartment(request.getDepartment());
        teacher.setDesignation(request.getDesignation());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setActive(true);
        return teacher;
    }
}