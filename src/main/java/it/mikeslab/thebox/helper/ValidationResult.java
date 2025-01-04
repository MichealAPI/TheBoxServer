package it.mikeslab.thebox.helper;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.CourseService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class ValidationResult {
    private final boolean success;
    private final ResponseEntity<String> errorResponse;
    private final BsonDocument bsonDocument;
    private final User user;

    public static ValidationResult failure(ResponseEntity<String> errorResponse) {
        return new ValidationResult(false, errorResponse, null, null);
    }

    public static ValidationResult success(BsonDocument bsonDocument, User user) {
        return new ValidationResult(true, null, bsonDocument, user);
    }

    /**
     * Validates the provided payload and the authenticated user. The method checks if the payload
     * is non-null, parses it into a BSON document, and verifies the presence of a valid courseId
     * within the payload. Additionally, it ensures that the authenticated user is present and valid.
     * If the validation is successful, the corresponding BSON document and user are returned as part of
     * the result. Otherwise, an error response is included in the validation result.
     *
     * @param payload         the JSON payload received in string format
     * @param courseService   the service used to interact with course data and validate the courseId
     * @return a {@code ValidationResult} indicating success or failure. On success, it includes the BSON
     *         document and user; on failure, it includes an appropriate error message.
     */
    public static ValidationResult validatePayloadAndUser(String payload, CourseService courseService) {
        Optional<BsonDocument> bson = Optional.ofNullable(
                BsonDocument.parse(payload)
        );

        if (bson.isEmpty()) {
            return ValidationResult.failure(
                    ResponseEntity.badRequest().body("Payload is required")
            );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object objUser = (auth != null) ? auth.getPrincipal() : null;

        if (objUser == null) {
            return ValidationResult.failure(
                    ResponseEntity.badRequest().body("User not found, refresh your session")
            );
        }

        BsonDocument document = bson.get();
        User user = (User) objUser;

        String courseId = document.getString("courseId").getValue();
        if (courseService.fetchCourseById(courseId) == null) {
            return ValidationResult.failure(
                    ResponseEntity.badRequest().body("Course not found")
            );
        }

        // Return validation success with the document and user
        return ValidationResult.success(document, user);
    }

}