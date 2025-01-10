const uploadAreas = document.querySelectorAll('.upload-area');

uploadAreas.forEach(uploadArea => {

    apply(uploadArea);

    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('active');
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();

        // Get dragged file data
        const file = e.dataTransfer.items[0].getAsFile();

        // Check if the file is an image
        if (file.type.startsWith('image')) { // Maybe add more types later
            uploadArea.classList.add('active');
        } else {
            uploadArea.classList.add('invalid');
        }

        // Check if the file is too big
        if (file.size > 10000000) { // 10MB
            uploadArea.classList.add('invalid');
        }

        // Transform into a binary string
        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = () => {
            const binary = reader.result;

            // Get category from attributes
            const targetCategory = uploadArea.getAttribute('data-category');
            const targetId = uploadArea.getAttribute('data-id');
            const field = uploadArea.getAttribute('data-field');
            let nestedReferenceId = null;

            if(uploadArea.hasAttribute('data-nested-reference-id')) {
                nestedReferenceId = uploadArea.getAttribute('data-nested-reference-id');
            }

            // Upload the image
            upload(
                targetCategory,
                targetId,
                field,
                binary,
                nestedReferenceId
            );
        };

    });

});

function upload(targetCategory, targetId, field, binary, nestedReferenceId) {
    const formData = new FormData();
    formData.append('targetId', targetId);
    formData.append('field', field);
    formData.append('binary', binary);
    if (nestedReferenceId != null) {
        formData.append('nestedReferenceId', nestedReferenceId);
    }

    return fetch(`/upload/${targetCategory}`, {
        method: 'POST',
        body: formData
    });
}

function download(targetCategory, targetId, field, nestedReferenceId) {

    const formData = createForm(targetId, field, nestedReferenceId);

    return fetch(`/download/${targetCategory}`, {
        method: 'GET',
        body: formData
    });
}


function createForm(category, targetId, field, nestedReferenceId) {
    const formData = new FormData();
    formData.append("category", category)
    formData.append('targetId', targetId);
    formData.append('field', field);
    if (nestedReferenceId != null) {
        formData.append('nestedReferenceId', nestedReferenceId);
    }

    return formData;
}


function apply(element) {

    const targetCategory = element.getAttribute('data-category');
    const targetId = element.getAttribute('data-id');
    const field = element.getAttribute('data-field');
    const nestedReferenceId = element.getAttribute('data-nested-reference-id');
    const defaultImage = element.getAttribute('data-default-image');

    download(targetCategory, targetId, field, nestedReferenceId)
        .then(response => {
            console.log(response);
        })
        .then(data => {
            console.log(data);

            const binary = data.binary;
            const base64 = decodeBase64(binary);

            element.style.backgroundImage = `url(${base64})`;

            console.log('Image downloaded');
            console.log(base64);

        }).catch(() => {
            element.style.backgroundImage = `url(${defaultImage})`;

            console.log('Image not found');

            console.log(defaultImage);
        });
}

function decodeBase64(base64) {
    return atob(base64);
}

