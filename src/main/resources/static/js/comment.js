
const postBtn = document.getElementById('postBtn');
const commentInput = document.getElementById('commentInput');

postBtn.addEventListener('click', async (e) => { // Make the listener function `async`

    if (commentInput.value === '') {
        showToast(
            "Error",
            ["bi", "bi-x-circle"],
            "Error",
            "red-color",
            3000,
            "Comment is required!"
        );
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);

    const courseId = urlParams.get('courseId');
    const ideaId = urlParams.get('ideaId');

    let username = e.target.getAttribute('data-username');

    let content = commentInput.value;
    commentInput.value = '';

    if (isAnonymous()) {

        showToast(
            "Info",
            ["bi", "bi-info-circle"],
            "Info",
            "blue-color",
            30000,
            "Anonymizing comment..."
        )

        username = 'Anonymous';
        content = await performAnonymization(content); // Await the result here
    }

    const payload = {
        courseId: courseId,
        ideaId: ideaId,
        username: username,
        content: content
    };

    fetch('/api/ideas/comment/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then((response) => {
            if (!response.ok) {
                response.body.getReader().read().then(({ value }) => {
                    const message = new TextDecoder().decode(value);
                    throw new Error(message);
                });
            }
        })
        .then(data => {
            showToast(
                "Success",
                ["bi", "bi-check-circle"],
                "Success",
                "green-color",
                3000,
                "Comment created successfully!"
            );
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            showToast(
                "Error",
                ["bi", "bi-x-circle"],
                "Error",
                "red-color",
                3000,
                error.message
            );
        });
});


function performAnonymization(text) {
    postBtn.disabled = true;
    anonymousSwitcher.disabled = true

    return anonymize(text)
        .then((response) => {
            postBtn.disabled = false;
            anonymousSwitcher.disabled = false
            console.log(response)
            return response.response; // Return anonymized text
        })
        .catch((error) => {
            postBtn.disabled = false;
            anonymousSwitcher.disabled = false
            console.error('Error during anonymization:', error);
            showToast(
                "Error",
                ["bi", "bi-x-circle"],
                "Error",
                "red-color",
                3000,
                "Anonymization failed!"
            );
            return text; // Return original text if anonymization fails
        });
}