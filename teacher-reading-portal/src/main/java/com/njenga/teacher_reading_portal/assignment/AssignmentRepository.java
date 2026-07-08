package com.njenga.teacher_reading_portal.assignment;

import com.njenga.teacher_reading_portal.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByTeacher(User teacher);

    List<Assignment> findByStudent(User student);
}
