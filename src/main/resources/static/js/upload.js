const uploadAreas = document.querySelectorAll('.upload-area');
const overlay = document.querySelectorAll('.overlay');

uploadAreas.forEach(uploadArea => {

    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('active');
    });

    uploadArea.addEventListener('dragleave', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('active');
        uploadArea.classList.remove('invalid');
    });

    uploadArea.addEventListener('drop', (e) => {

        e.preventDefault();

        if(uploadArea.hasAttribute('uploading')) {
            return;
        }

        setTimeout(() => {
            uploadArea.classList.remove('active');
            uploadArea.classList.remove('invalid');
        }, 400);
    })

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();

        // Get dragged file data
        const file = e.dataTransfer.items[0].getAsFile();

        // Check if the file is an image
        if (file.type.startsWith('image')) { // Maybe add more types later
            uploadArea.classList.add('active');
        } else {
            uploadArea.classList.add('invalid');
            return;
        }

        // Check if the file is too big
        if (file.size > 2000000) { // 2MB
            uploadArea.classList.add('invalid');
            return;
        }

        // Get category from attributes
        const targetCategory = uploadArea.getAttribute('data-category');
        const targetId = uploadArea.getAttribute('data-id');
        const field = uploadArea.getAttribute('data-field');
        let nestedReferenceId = null;

        if(uploadArea.hasAttribute('data-nested-reference-id')) {
            nestedReferenceId = uploadArea.getAttribute('data-nested-reference-id');
        }

        // Show loader animation while uploading
        uploadArea.classList.add('d-flex');
        uploadArea.classList.add('align-items-center');
        uploadArea.classList.add('justify-content-center');
        uploadArea.innerHTML = `<div class="spinner-border green-color" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>`

        uploadArea.setAttribute("uploading", "true");

        // Upload the image
        upload(
            targetCategory,
            targetId,
            field,
            file,
            nestedReferenceId
        ).then(() => location.reload());

    });

});

function upload(targetCategory, targetId, field, file, nestedReferenceId) {
    const formData = new FormData();
    formData.append('targetId', targetId);
    formData.append('field', field);
    formData.append('file', file);
    if (nestedReferenceId != null) {
        formData.append('nestedReferenceId', nestedReferenceId);
    }

    return fetch(`/upload/${targetCategory}`, {
        method: 'POST',
        body: formData
    });
}

if (overlay) {
    overlay.forEach(overlay => {
        overlay.addEventListener('click', () => {
            const fileInput = document.createElement('input');
            fileInput.type = 'file';
            fileInput.accept = 'image/*';
            fileInput.style.display = 'none';

            fileInput.addEventListener('change', (e) => {
                const file = e.target.files[0];
                if (file) {
                    const event = new DragEvent('drop', {
                        dataTransfer: new DataTransfer()
                    });
                    event.dataTransfer.items.add(file);
                    //.dispatchEvent(event);
                    overlay.parentElement.dispatchEvent(event);
                }
            });

            document.body.appendChild(fileInput);
            fileInput.click();
            document.body.removeChild(fileInput);
        });
    });
}