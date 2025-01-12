
const anonymousSwitcher = document.getElementById('anonymousSwitcher');
const anonymousCheckbox = document.getElementById('anonymousCheckbox');
const icon = document.getElementById('switcher-icon');

anonymousSwitcher.addEventListener('change', (e) => {
    const commentInput = document.getElementById('commentInput');

    if (e.target.checked) {
        commentInput.setAttribute('placeholder', 'Write a comment as an anonymous user');
    } else {
        commentInput.setAttribute('placeholder', 'Write a comment...');
    }
});

function isAnonymous() {
    return anonymousCheckbox.checked;
}

async function anonymize(text) {
    try {
        const response = await fetch('/anonymize', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                comment: text
            })
        });

        const { value } = await response.body.getReader().read();
        const message = new TextDecoder().decode(value);

        if (!response.ok) {
            throw new Error(message);
        }

        console.log(message);

        return message;
    } catch (error) {
        console.error('Error during anonymization:', error);
        throw error; // Re-throw the error to let the caller handle it
    }
}
