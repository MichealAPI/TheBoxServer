package it.mikeslab.thebox.repository;

import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {

    Course findByTitle(String title);
    Course findByOwnerUsername(String username);

    List<Course> findCoursesByMembersContaining(String username);

}
