document.getElementById("submitInvite").addEventListener("click", function () {
    // Get the email input value
    const targetEmail = document.getElementById("targetEmail").value;

    // Validate the input (check if the value is not empty and is a valid email address)
    if (!targetEmail) {
        showToast(
            "Error",
            ["bi", "bi-x-circle"],
            "Error",
            "red-color",
            3000,
            "Email address is required!"
        );
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(targetEmail)) {
        showToast(
            "Error",
            ["bi", "bi-x-circle"],
            "Error",
            "red-color",
            3000,
            "Invalid email address format!"
        );
        return;
    }

    // Retrieve the courseId from the data attribute of the inviteModal
    const inviteModal = document.getElementById("inviteModal");
    const courseId = inviteModal.getAttribute("data-course-id");

    if (!courseId) {
        showToast(
            "Error",
            ["bi", "bi-x-circle"],
            "Error",
            "red-color",
            3000,
            "Course identifier is missing!"
        );
        return;
    }

    // Prepare the payload for the POST request
    const payload = {
        courseId: courseId,
        target: targetEmail
    };

    showToast(
        "Info",
        ["bi", "bi-info-circle"],
        "Info",
        "blue-color",
        3000,
        "Sending invitation..."
    );

    // Send the POST request to the backend
    fetch('/invite', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((errorMessage) => {
                    throw new Error(errorMessage);
                });
            }
            return response;
        })
        .then(() => {
            // Show a success toast message
            showToast(
                "Success",
                ["bi", "bi-check-circle"],
                "Success",
                "green-color",
                3000,
                "Invitation sent successfully!"
            );

            // Clear input after successful submission
            document.getElementById("targetEmail").value = "";
        })
        .catch((error) => {
            // Show an error toast message
            console.error("Error:", error);
            showToast(
                "Error",
                ["bi", "bi-x-circle"],
                "Error",
                "red-color",
                3000,
                error.message || "Failed to send invitation!"
            );
        });
});