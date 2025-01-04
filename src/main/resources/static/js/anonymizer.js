
const anonymousSwitcher = document.getElementById('anonymousSwitcher');
const icon = document.getElementById('switcher-icon');

let status = anonymousSwitcher.getAttribute('data-checked');

anonymousSwitcher.addEventListener('click', (e) => {
    const commentInput = document.getElementById('commentInput');
    status = status === 'true' ? 'false' : 'true';

    if (status === 'true') {
        commentInput.setAttribute('placeholder', 'Write a comment as an anonymous user');
    } else {
        commentInput.setAttribute('placeholder', 'Write a comment...');
    }

    anonymousSwitcher.classList.toggle('bg-green');
    anonymousSwitcher.classList.toggle('bg-black');

    icon.classList.toggle('bi-person-fill-x');
    icon.classList.toggle('bi-person-fill');

    anonymousSwitcher.setAttribute('data-checked', status);

})

function isAnonymous() {
    return status === 'true';
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