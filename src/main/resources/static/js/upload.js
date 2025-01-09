const uploadAreas = document.querySelectorAll('.upload-area');

uploadAreas.forEach(uploadArea => {

    uploadArea.addEventListener('dragover', (e) => {
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
            uploadArea(
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