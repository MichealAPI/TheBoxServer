package it.mikeslab.thebox.repository;

import it.mikeslab.thebox.entity.Idea;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeaRepository extends MongoRepository<Idea, String> {

    Idea findByTitle(String title);
    Idea findByUserId(String userId);

    List<Idea> findIdeasByCourseId(String courseId);

}
