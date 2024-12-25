package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourseByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public Course getCourseByAuthor(String authorId) {
        return courseRepository.findByOwnerId(authorId);
    }

    public boolean deleteCourse(String title) {
        Course course = courseRepository.findByTitle(title);
        if (course != null) {
            courseRepository.delete(course);
            return true;
        }
        return false;
    }

    public List<Course> getCoursesByMember(String userId) {
        return courseRepository.findCoursesByMembersContaining(userId);
    }

}
