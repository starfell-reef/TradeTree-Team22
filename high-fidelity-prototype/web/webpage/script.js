const modal = document.getElementById('create-profile-modal');
const openButton = document.getElementById('open-create-profile');
const closeButton = document.getElementById('close-create-profile');

if (modal && openButton && closeButton) {
    openButton.addEventListener('click', function () {
        modal.setAttribute('aria-hidden', 'false');
        modal.style.display = 'block';
    });

    closeButton.addEventListener('click', function () {
        modal.setAttribute('aria-hidden', 'true');
        modal.style.display = 'none';
    });

    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.setAttribute('aria-hidden', 'true');
            modal.style.display = 'none';
        }
    });
}
