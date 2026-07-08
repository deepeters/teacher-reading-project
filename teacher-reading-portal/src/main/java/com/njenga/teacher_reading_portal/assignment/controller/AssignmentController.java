package com.njenga.teacher_reading_portal.assignment.controller;

import com.njenga.teacher_reading_portal.assignment.dto.AssignmentResponse;
import com.njenga.teacher_reading_portal.assignment.dto.CreateAssignmentRequest;
import com.njenga.teacher_reading_portal.assignment.dto.UpdateAssignmentProgressRequest;
import com.njenga.teacher_reading_portal.assignment.service.AssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public AssignmentResponse createAssignment(
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        return assignmentService.createAssignment(request);
    }

    @GetMapping("/teacher")
    public List<AssignmentResponse> getTeacherAssignments() {
        return assignmentService.getTeacherAssignments();
    }

    @GetMapping("/student")
    public List<AssignmentResponse> getStudentAssignments() {
        return assignmentService.getStudentAssignments();
    }

    @PatchMapping("/{id}/progress")
    public AssignmentResponse updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentProgressRequest request
    ) {
        return assignmentService.updateProgress(id, request);
    }
}
