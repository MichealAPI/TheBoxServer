document.getElementById("submitCourse").addEventListener("click", function () {
    // Get the form data
    const name = document.getElementById("courseName").value;
    const description = document.getElementById("courseDescription").value;

    // Create the body of the POST request
    const requestData = {
        title: name,
        description: description
    };

    // Send POST request using Fetch API
    fetch('/api/courses/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {

            console.log(response)

            if (!response.ok) {
                throw new Error('Failed to create course');
            }

        })
        .then(data => {
            // Handle success

            showToast(
                "Success",
                ["bi", "bi-check-circle"],
                "Success",
                "green-color",
                3000,
                "Course created successfully!"
            );

            location.reload();
        })
        .catch(error => {
            // Handle errors
            console.error('Error:', error);
            showToast(
                "Error",
                ["bi", "bi-x-circle"],
                "Error",
                "red-color",
                3000,
                "Failed to create course!"
            );
        });
});