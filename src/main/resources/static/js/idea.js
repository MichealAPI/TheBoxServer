document.getElementById("submitIdea").addEventListener("click", function () {
    // Get the new form data
    const name = document.getElementById("ideaName").value; // Updated field selector
    const details = document.getElementById("ideaDetails").value; // Updated field selector

    // Find course id from param courseId
    const urlParams = new URLSearchParams(window.location.search);

    // Create the body of the POST request for the new API
    const requestData = {
        name: name,
        details: details, // Updated field name
        courseId: urlParams.get('courseId')
    };

    // Send POST request to the new endpoint
    fetch('/api/ideas/create', { // Updated endpoint
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {

            if (!response.ok) {
                throw new Error('Failed to create idea');
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
                "Idea created successfully!"
            );

            location.reload(); // Reload to show updates
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
                "Failed to create idea!"
            );
        });
});