const modal = document.getElementById('create-profile-modal');
const openButton = document.getElementById('open-create-profile');
const closeButton = document.getElementById('close-create-profile');

if (modal && openButton && closeButton) {
    openButton.addEventListener('click', function () {
        modal.hidden = false;
        modal.querySelector('input').focus();
    });

    closeButton.addEventListener('click', function () {
        modal.hidden = true;
        openButton.focus();
    });

    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.hidden = true;
            openButton.focus();
        }
    });

    window.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && !modal.hidden) {
            modal.hidden = true;
            openButton.focus();
        }
    });
}

const loginForm = document.getElementById('login-form');
const createProfileForm = document.getElementById('create-profile-form');

async function sendApiRequest(apiBaseUrl, path, options) {
    let response;
    try {
        response = await fetch(`${apiBaseUrl}${path}`, {
            ...options,
            credentials: 'omit',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });
    } catch {
        throw new Error('Unable to connect to TradeTree. Make sure the backend is running.');
    }

    if (!response.ok) {
        let message = 'The request could not be completed.';
        try {
            const body = await response.json();
            message = body.error || body.detail || message;
        } catch {
            // Use the generic message if the server did not return JSON.
        }
        throw new Error(message);
    }

    return response.status === 204 ? null : response.json();
}

if (loginForm && createProfileForm) {
    const apiBaseUrl = document.querySelector('meta[name="api-base-url"]')?.content
        || 'http://localhost:8080';
    const authStatus = document.getElementById('auth-status');
    const createProfileStatus = document.getElementById('create-profile-status');
    const loginPassword = document.getElementById('password');

    function setStatus(element, message, type) {
        element.textContent = message;
        element.dataset.status = type;
    }

    loginForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        const submitButton = loginForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        setStatus(authStatus, 'Signing in...', 'info');
        try {
            const profile = await sendApiRequest(apiBaseUrl, '/api/users/login', {
                method: 'POST',
                body: JSON.stringify({
                    username: document.getElementById('username').value,
                    password: loginPassword.value
                })
            });
            loginPassword.value = '';
            setStatus(authStatus, `Sign-in successful. Welcome, ${profile.username}.`, 'success');
        } catch (error) {
            setStatus(authStatus, error.message, 'error');
        } finally {
            submitButton.disabled = false;
        }
    });

    createProfileForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        const submitButton = createProfileForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        setStatus(createProfileStatus, 'Creating your profile...', 'info');
        try {
            const profile = await sendApiRequest(apiBaseUrl, '/api/users', {
                method: 'POST',
                body: JSON.stringify({
                    username: document.getElementById('new-username').value,
                    name: document.getElementById('new-name').value,
                    email: document.getElementById('new-email').value,
                    password: document.getElementById('new-password').value
                })
            });
            document.getElementById('new-password').value = '';
            document.getElementById('username').value = profile.username;
            modal.hidden = true;
            createProfileForm.reset();
            setStatus(authStatus, 'Profile created successfully. Sign in with your username and password.', 'success');
            document.getElementById('password').focus();
        } catch (error) {
            setStatus(createProfileStatus, error.message, 'error');
        } finally {
            submitButton.disabled = false;
        }
    });

}

const analyticsForm = document.getElementById('analytics-login-form');

if (analyticsForm) {
    const apiBaseUrl = document.querySelector('meta[name="api-base-url"]')?.content
        || 'http://localhost:8080';
    const status = document.getElementById('analytics-status');
    const results = document.getElementById('analytics-results');

    function setAnalyticsStatus(message, type) {
        status.textContent = message;
        status.dataset.status = type;
    }

    analyticsForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        const submitButton = analyticsForm.querySelector('button[type="submit"]');
        const passwordInput = document.getElementById('admin-password');
        submitButton.disabled = true;
        results.hidden = true;
        setAnalyticsStatus('Loading analytics...', 'info');

        try {
            const analytics = await sendApiRequest(apiBaseUrl, '/api/admin/analytics', {
                method: 'POST',
                body: JSON.stringify({
                    username: document.getElementById('admin-username').value,
                    password: passwordInput.value
                })
            });
            passwordInput.value = '';
            for (const period of ['week', 'month', 'year']) {
                const values = analytics[period];
                document.getElementById(`${period}-accounts`).textContent = values.accountsCreated;
                document.getElementById(`${period}-active`).textContent = values.activeUsers;
                document.getElementById(`${period}-dates`).textContent =
                    `${values.startDate} through ${values.endDate} (UTC)`;
            }
            results.hidden = false;
            setAnalyticsStatus('Analytics loaded.', 'success');
        } catch (error) {
            passwordInput.value = '';
            setAnalyticsStatus(error.message, 'error');
        } finally {
            submitButton.disabled = false;
        }
    });
}
