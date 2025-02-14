package it.mikeslab.thebox.service;

import it.mikeslab.thebox.dto.IdeaDTO;
import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CacheManager cacheManager;

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public void addInvite(Course course, String username, String inviteId) {
        if (course != null) {
            course.getInvites().put(inviteId, username);
            courseRepository.save(course);
        }
    }

    public void removeInvite(Course course, String inviteId) {
        if (course != null) {
            course.getInvites().remove(inviteId);
            courseRepository.save(course);
        }
    }

    public void addMember(Course course, String userId) {
        if (course != null) {
            course.getMembers().add(userId);
            courseRepository.save(course);
        }
    }

    public boolean upsertIdea(String courseId, Idea idea) {

        Course course = fetchCourseById(courseId);

        if (course != null && course.getIdeas() != null) {
            course.getIdeas().put(idea.getId(), idea);

            // Invalidate cache
            if(cacheManager.getCacheNames().contains("ideas")) {
                cacheManager
                        .getCache("ideas")
                        .evictIfPresent(courseId);
            }

            courseRepository.save(course);
            return true;
        }
        return false;
    }

    @Cacheable(value = "ideas", key = "#ideaId")
    public Idea getIdea(String ideaId, String courseId) {
        Course course = fetchCourseById(courseId);
        if (course != null) {
            return course.getIdeas().get(ideaId);
        }
        return null;
    }


    public List<IdeaDTO> getAllIdeasByCourseId(String courseId) {
        Course course = fetchCourseById(courseId);
        if (course == null) {
            return new ArrayList<>();
        }
        return course.getIdeas().values().stream()
                .map(idea -> mapIdeaToDTO(idea, courseId))
                .filter(java.util.Objects::nonNull)
                .toList();
    }


    public List<Course> fetchCoursesByMember(String username) {
        return courseRepository.findCoursesByMembersContaining(username);
    }

    @Cacheable(value = "courses", key = "#courseId")
    public Course fetchCourseById(String courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    private boolean isValidCourse(Course course) {
        return course != null && course.getIdeas() != null;
    }

    public void saveCourse(Course course) {
        if (isValidCourse(course)) {

            // Invalidate cache
            if(cacheManager.getCacheNames().contains("courses")) {
                cacheManager
                        .getCache("courses")
                        .evictIfPresent(course.getId());
            }

            courseRepository.save(course);
        }
    }


    private IdeaDTO mapIdeaToDTO(Idea idea, String courseId) {
        User author = userService.getUserByUsername(idea.getAuthorUsername());
        if (author == null) {
            return null;
        }
        return new IdeaDTO(
                idea,
                courseId,
                author.getUsername(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}
