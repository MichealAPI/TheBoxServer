
const anonymousSwitcher = document.getElementById('anonymousSwitcher');
const icon = document.getElementById('switcher-icon');

anonymousSwitcher.addEventListener('change', (e) => {
    const commentInput = document.getElementById('commentInput');

    console.log("Should be working!");

    if (e.currentTarget.checked) {
        commentInput.setAttribute('placeholder', 'Write a comment as an anonymous user');
    } else {
        commentInput.setAttribute('placeholder', 'Write a comment...');
    }
});

function isAnonymous() {
    return anonymousSwitcher.checked;
}

async function anonymize(text) {
    try {
        const response = await fetch('http://localhost:11434/api/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                model: "llama3.2",
                prompt: "Make this text anonymous by removing all personal or specific details. Rewrite it naturally as if someone is speaking directly, without adding quotation marks or unnecessary formatting. Provide only the revised text: " + text,
                stream: false
            })
        });

        if (!response.ok) {
            const { value } = await response.body.getReader().read();
            const message = new TextDecoder().decode(value);
            throw new Error(message);
        }

        return await response.json();
    } catch (error) {
        console.error('Error during anonymization:', error);
        throw error; // Re-throw the error to let the caller handle it
    }
}