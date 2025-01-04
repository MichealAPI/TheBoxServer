
const voteButtons = document.querySelectorAll('.vote-btn');

function vote(e) {

    e.preventDefault();

    const ideaId = e.currentTarget.getAttribute('data-idea-id');
    const vote = e.currentTarget.getAttribute('data-vote');
    const courseId = e.currentTarget.getAttribute('data-course-id');

    // Create the body of the POST request to '/api/ideas/vote'
    const requestData = {
        ideaId: ideaId,
        vote: vote,
        courseId: courseId
    };

    // Send POST request to the endpoint
    fetch('/api/ideas/vote', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {

            response.body.getReader().read().then(({ value }) => {

                const message = new TextDecoder().decode(value);

                if (!response.ok) {
                    showToast(
                    "Error",
                    ["bi", "bi-x-circle"],
                    "Error",
                    "red-color",
                    3000,
                        message
                    );
                    return;
                }

                // Handle success
                showToast(
                    "Success",
                    ["bi", "bi-check-circle"],
                    "Success",
                    "green-color",
                    3000,
                    "Voted successfully!"
                );

                // Find the idea card and update the votes
                const ideaCard = document.getElementById(`idea-${ideaId}`);

                // Find the vote count element
                const voteCount = ideaCard.querySelector('.vote-count');

                // Find the vote count value by parsing the POST response
                voteCount.innerText = message;

            });
        });

}


voteButtons.forEach(button => {

    button.addEventListener('click', vote);

});
