package it.mikeslab.thebox.service;

import it.mikeslab.thebox.dto.IdeaDTO;
import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourse(String courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public Course getCourseByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public Course getCourseByAuthor(String username) {
        return courseRepository.findByOwnerUsername(username);
    }

    public boolean deleteCourse(String title) {
        Course course = courseRepository.findByTitle(title);
        if (course != null) {
            courseRepository.delete(course);
            return true;
        }
        return false;
    }

    public Idea addIdeaToCourse(String courseId, Idea idea) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.getIdeas().add(idea);
            courseRepository.save(course);
            return idea;
        }
        return null;
    }

    public List<IdeaDTO> getAllIdeasWithAuthors(String courseId) {

        Course course = this.getCourse(courseId);

        List<IdeaDTO> ideas = new ArrayList<>();

        for(Idea idea : course.getIdeas()) {

            // Retrieve author as User

            User author = userService.getUserByUsername(idea.getAuthorUsername());

            if (author == null) {
                continue;
            }

            ideas.add(
                    new IdeaDTO(
                            idea,
                            author.getUsername(),
                            author.getFirstName(),
                            author.getLastName()
                    ));

        }

        return ideas;
    }

    public List<Course> getCoursesByMember(String userId) {
        return courseRepository.findCoursesByMembersContaining(userId);
    }
}
