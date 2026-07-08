package com.njenga.teacher_reading_portal.assignment.dto;

import com.njenga.teacher_reading_portal.assignment.AssignmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateAssignmentProgressRequest(

        @NotNull
        AssignmentStatus status,

        @NotNull
        @Min(0)
        Integer minutesRead

) {
}
