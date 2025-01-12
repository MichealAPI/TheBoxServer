# The Box

**TheBox** is a web application designed to enhance collaboration between teachers and students by facilitating the exchange of ideas within courses. Whether it’s planning memorable events, organizing fun activities, or improving the coursework experience, the platform enables users to propose, vote, and comment on ideas in an intuitive, interactive way. Initially targeted at schools, the platform is built to be scalable for adoption in a variety of other environments such as businesses and community organizations.

---

## Test Credentials

Use the following test credentials to explore the application:

- **Email**: `andrew.kosatka@gmail.com`
- **Password**: `test`

---

## Key Features

- **Proposals and Voting**:  
  Teachers and students can propose ideas within a course. Each idea includes:
    - Title
    - Description
    - Overall score based on user votes
    - Proposer
    - Comments

- **Commenting Options**:  
  Comment publicly or anonymously on ideas. Anonymous comments are processed through **AI (for a Free & Open-source version, see [Llama](https://www.llama.com/))** to ensure neutral and constructive formatting.

- **Email Verification & Course Invitations**:  
  During registration, emails are verified to prevent unauthorized access. Course invitations work via email: users receive a unique link that redirects to the login page and automatically adds them to the relevant course upon authentication.

- **Interactive & Secure User Interface**:
    - User-friendly and intuitive registration and login pages with smooth and interactive forms.
    - Robust security for login and user management.

- **Entity-Based Design**:  
  The application revolves around four main entities:
    - **User**
    - **Course**
    - **Idea**
    - **Comment**

- **Expandability**:  
  Although currently optimized for schools, the program design supports scalability for use in corporate training, community programs, or other activity-driven groups.

---

## Technologies Used

The platform leverages the following technologies:

- **Backend**: Jakarta EE (using `jakarta` imports) combined with **Spring Data Mongo**.
- **Frontend**: Thymeleaf templates with rich **CSS/HTML/Bootstrap** styling.
- **Database**: MongoDB for storing application data.
- **Authentication**: Managed using secure email-based verification.
- **AI Integration**: Filters for anonymous comments to enforce neutral formatting.
- **Programming Language**: Java (Java SDK 21).

---

## Installation Instructions

Follow these steps to set up the application locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/MichealAPI/TheBoxServer.git
   cd idea-sharing-platform
   ```

2. **Backend Setup**:
    - Install Java 21 if not already installed.
    - Install MongoDB locally or use a cloud-based MongoDB instance.
    - Adjust your application properties (typically in `application.yaml` or `application.properties`) to connect to the database. Use placeholders below:
      ```yaml
      spring:
        data:
          mongodb:
            uri: <your-mongodb-uri>
      ```

3. **Frontend Setup**:
    - No special setup is required if using embedded Thymeleaf. Ensure relevant static resources (CSS/JS) are available in the project structure.

4. Build and Run:  
   Use **IntelliJ IDEA** or any preferred IDE with Jakarta EE support.  
   You can manually build and start your application through the terminal:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Open the application in your browser:  
   By default, the application runs on `http://localhost:8080`.

6. Note: Some addresses in links may need to be adjusted based on your local setup.

---

## Usage Examples

1. **Registration and Course Invitations**:
    - Visit the `/register` endpoint to create an account.
    - Verify your email to proceed.
    - Use the invite link sent from a course to join directly.

2. **Proposing an Idea**:
    - Navigate to the course page you belong to.
    - Use the "Propose Idea" button to create a new idea. Input:
        - Title
        - Description

3. **Voting**:
    - Each idea displays voting options. Click upvote or downvote to express your opinion.

4. **Commenting**:
    - Post comments either anonymously or publicly for any idea. Select the anonymous option for AI-managed comment neutrality.

---

## Example Entities

Here are examples of the primary entities used in the system:

### User
```java
@Data
@Document
public class User {
  @Id
  private String id;
  private String name;
  private String email;
  private List<String> courseIds;
}
```

### Course
```java
@Data
@Document
public class Course {
  @Id
  private String id;
  private String name;
  private List<String> userIds;
  private List<Idea> ideas;
}
```

### Idea
```java
@Data
@Document
public class Idea {
  @Id
  private String id;
  private String title;
  private String description;
  private String proposerId;
  private int score;
  private List<Comment> comments;
}
```

### Comment
```java
@Data
@Document
public class Comment {
  @Id
  private String id;
  private String content;
  private boolean isAnonymous;
  private Instant timestamp;
}
```

---

## Planned Features

The following enhancements are planned for future releases:

- **Notification System**:  
  Automatic email notifications for new ideas, votes, or comments.

- **Role-Based Access Control**:  
  Differentiated permissions for teachers, students, and course administrators.

- **Advanced Search Filters**:
  Search and sort ideas based on score, date posted, or other metrics.

- **Mobile App**:  
  Release a mobile version of the platform for Android and iOS.

- **Enhanced Scalability**:  
  Optimizations to deploy the platform to enterprise-level environments.

---

## Known Issues
As for now, AI hosting isn't free, so the anonymous comments feature is disabled for the demo version.
To enable it, you need to host the AI service yourself and update the URL in the `anonymizer.js` javascript class.

See more at [Llama](https://www.llama.com/)

```javascript
        const response = await fetch('http://localhost:11434/api/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                model: "llama3.2",
                prompt: "Make this text anonymous by removing all personal or specific details. Rewrite it naturally as if someone is speaking directly, without adding quotation marks or unnecessary formatting. Provide only the revised text: " + text,
                stream: false
            })
        });
```

---

## Contributions

Contributions from the community are well appreciated. If you'd like to suggest a feature or fix a bug:

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add new feature X"
   ```
4. Push your changes:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

For questions, feedback, or feature requests, please contact me at:  
`m@mikeslab.it`

---

I hope this program empowers educators, students, and communities to share and collaborate on innovative ideas like never before!