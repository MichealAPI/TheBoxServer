const supportBtn = document.querySelectorAll('.support-btn');
const supportSection = document.getElementById('supportSection');
const supportCloseBtn = document.getElementById('supportCloseBtn');
const supportMessage = document.getElementById('supportMessage');
const supportEmail = document.getElementById('supportEmail');
const supportSendBtn = document.getElementById('supportSendBtn');

let isSupportVisible = false;

supportCloseBtn.addEventListener('click', () => {
    supportSection.classList.add('d-none');
    isSupportVisible = false;
});

supportBtn.forEach((btn) => {
    btn.addEventListener('click', () => {
        supportSection.classList.toggle('d-none');
        isSupportVisible = !isSupportVisible;
    });
});

// Handle the Escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && isSupportVisible) {
        supportSection.classList.add('d-none');
        isSupportVisible = false;
    }
});

// Handle logic
supportSendBtn.addEventListener('click', () => {
    if (supportMessage.value && supportEmail.value) {
        showToast(
            'Info',
            ['bi', 'bi-info-circle'],
            'Info',
            'blue-color',
            2000,
            'Sending support message...'
        );

        setTimeout(() => {
            supportSection.classList.add('d-none');
            isSupportVisible = false;
        }, 2000); // Todo: Replace with actual API call
    } else {
        alert('Please fill in both fields!');
    }
});