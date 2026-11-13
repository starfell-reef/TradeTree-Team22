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
            credentials: options.credentials || 'omit',
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
        const error = new Error(message);
        error.status = response.status;
        throw error;
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

const adminPage = document.body.dataset.adminPage === 'true';

if (adminPage) {
    const apiBaseUrl = document.querySelector('meta[name="api-base-url"]')?.content
        || 'http://localhost:8080';
    const gate = document.createElement('section');
    gate.className = 'admin-access-gate';
    gate.setAttribute('aria-labelledby', 'admin-access-title');
    gate.innerHTML = `
        <div class="admin-access-card">
            <h1 id="admin-access-title">Admin access required</h1>
            <p>Sign in with an administrator account to continue.</p>
            <form id="admin-access-form">
                <label for="admin-access-username">Admin username</label>
                <input id="admin-access-username" name="username" autocomplete="username"
                    maxlength="30" required>
                <label for="admin-access-password">Password</label>
                <input id="admin-access-password" name="password" type="password"
                    autocomplete="current-password" maxlength="128" required>
                <button type="submit">Sign in</button>
            </form>
            <p id="admin-access-status" role="status" aria-live="polite"></p>
            <a href="marketplace.html">Back to marketplace</a>
        </div>`;
    document.body.append(gate);

    const form = gate.querySelector('#admin-access-form');
    const usernameInput = gate.querySelector('#admin-access-username');
    const passwordInput = gate.querySelector('#admin-access-password');
    const accessStatus = gate.querySelector('#admin-access-status');
    usernameInput.focus();

    function setAccessStatus(message, type) {
        accessStatus.textContent = message;
        accessStatus.dataset.status = type;
    }

    function grantAdminAccess(username) {
        document.body.dataset.adminAuthorized = 'true';
        gate.hidden = true;
        const navigation = document.querySelector('.site-header nav ul')
            || document.querySelector('.site-header nav');
        if (navigation && !document.querySelector('.admin-logout')) {
            const logout = document.createElement('button');
            logout.type = 'button';
            logout.className = 'admin-logout';
            logout.textContent = `Sign out (${username})`;
            logout.addEventListener('click', async function () {
                logout.disabled = true;
                try {
                    await sendApiRequest(apiBaseUrl, '/api/admin/session', {
                        method: 'DELETE',
                        credentials: 'include'
                    });
                    document.body.dataset.adminAuthorized = 'false';
                    window.location.href = 'marketplace.html';
                } catch (error) {
                    logout.disabled = false;
                    window.alert(error.message);
                }
            });
            if (navigation.tagName === 'UL') {
                const item = document.createElement('li');
                item.append(logout);
                navigation.append(item);
            } else {
                navigation.append(logout);
            }
        }

        loadAdminAnalytics();
    }

    async function loadAdminAnalytics() {
        const status = document.getElementById('analytics-status');
        const results = document.getElementById('analytics-results');
        if (!status || !results) {
            return;
        }

        status.textContent = 'Loading analytics...';
        status.dataset.status = 'info';
        try {
            const analytics = await sendApiRequest(apiBaseUrl, '/api/admin/analytics', {
                method: 'GET',
                credentials: 'include'
            });
            for (const period of ['week', 'month', 'year']) {
                const values = analytics[period];
                document.getElementById(`${period}-accounts`).textContent = values.accountsCreated;
                document.getElementById(`${period}-active`).textContent = values.activeUsers;
                document.getElementById(`${period}-dates`).textContent =
                    `${values.startDate} through ${values.endDate} (UTC)`;
            }
            results.hidden = false;
            status.textContent = 'Analytics loaded.';
            status.dataset.status = 'success';
        } catch (error) {
            status.textContent = error.message;
            status.dataset.status = 'error';
            if (error.status === 401) {
                document.body.dataset.adminAuthorized = 'false';
                results.hidden = true;
                gate.hidden = false;
                passwordInput.value = '';
                setAccessStatus('Your admin session has expired. Please sign in again.', 'error');
            }
        }
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const submitButton = form.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        setAccessStatus('Verifying administrator account...', 'info');

        try {
            const session = await sendApiRequest(apiBaseUrl, '/api/admin/session', {
                method: 'POST',
                credentials: 'include',
                body: JSON.stringify({
                    username: usernameInput.value,
                    password: passwordInput.value
                })
            });
            passwordInput.value = '';
            grantAdminAccess(session.username);
        } catch (error) {
            passwordInput.value = '';
            setAccessStatus(error.status === 401
                ? 'Those credentials do not belong to an administrator account.'
                : error.message, 'error');
        } finally {
            submitButton.disabled = false;
        }
    });

    sendApiRequest(apiBaseUrl, '/api/admin/session', {
        method: 'GET',
        credentials: 'include'
    }).then(function (session) {
        grantAdminAccess(session.username);
    }).catch(function (error) {
        if (document.body.dataset.adminAuthorized === 'true') {
            return;
        }
        gate.hidden = false;
        setAccessStatus(error.status === 401
            ? 'Sign in with an administrator account to continue.'
            : error.message, error.status === 401 ? 'info' : 'error');
    });
}

const currentPage = window.location.pathname.split('/').pop() || 'index.html';

const reportDialog = document.querySelector('.report-dialog');
const reportForm = document.getElementById('report-form');

if (reportDialog && reportForm) {
    let reportOpener;
    const reportTitle = document.getElementById('report-dialog-title');
    const reportDetails = document.getElementById('report-details');
    const reportStatus = reportForm.querySelector('.report-status');

    document.querySelectorAll('[data-report-open]').forEach(function (button) {
        button.addEventListener('click', function () {
            reportOpener = button;
            const target = button.dataset.reportTarget;
            reportTitle.textContent = `Report ${target}`;
            reportDetails.placeholder = `Explain why you are reporting this ${target}`;
            reportStatus.textContent = '';
            reportForm.reset();
            reportDialog.showModal();
            document.getElementById('report-reason').focus();
        });
    });

    reportForm.querySelector('.report-cancel').addEventListener('click', function () {
        reportDialog.close();
    });

    reportDialog.addEventListener('close', function () {
        if (reportOpener) {
            reportOpener.focus();
        }
    });

    reportForm.addEventListener('submit', function (event) {
        event.preventDefault();
        reportStatus.textContent =
            'Report submission is not available yet. Your report has not been sent.';
    });
}

document.querySelectorAll('a[href]').forEach(function (link) {
    const destination = link.getAttribute('href').split(/[?#]/, 1)[0];
    if (destination === currentPage) {
        link.setAttribute('aria-current', 'page');
    }
});

const searchTargets = {
    'listing-search': '.listing-card',
    'profile-search': '.listing-card',
    'ticket-search': '.ticket-item',
    'conversation-search': '.conversation-item'
};

Object.entries(searchTargets).forEach(function ([inputId, targetSelector]) {
    const input = document.getElementById(inputId);
    if (!input) {
        return;
    }

    const items = Array.from(document.querySelectorAll(targetSelector));
    if (items.length === 0) {
        return;
    }

    if (inputId === 'listing-search' && document.getElementById('marketplace-search-form')) {
        return;
    }

    input.addEventListener('input', function () {
        const query = input.value.trim().toLocaleLowerCase();
        items.forEach(function (item) {
            item.hidden = !item.textContent.toLocaleLowerCase().includes(query);
        });
    });
});

const marketplaceSearchForm = document.getElementById('marketplace-search-form');

if (marketplaceSearchForm) {
    const listingInput = document.getElementById('listing-search');
    const zipInput = document.getElementById('zip-search');
    const listings = Array.from(document.querySelectorAll('.listing-grid .listing-card'));
    const status = document.getElementById('marketplace-search-status');

    marketplaceSearchForm.addEventListener('submit', function (event) {
        event.preventDefault();
        const query = listingInput.value.trim().toLocaleLowerCase();
        const zip = zipInput.value.trim();
        let visibleCount = 0;

        listings.forEach(function (listing) {
            const matchesText = !query || listing.textContent.toLocaleLowerCase().includes(query);
            const matchesZip = !zip || listing.dataset.zip === zip;
            listing.hidden = !matchesText || !matchesZip;
            if (!listing.hidden) {
                visibleCount += 1;
            }
        });

        status.hidden = visibleCount > 0;
        status.textContent = zip && query
            ? `No listings match "${listingInput.value.trim()}" in ZIP code ${zip}.`
            : zip
                ? `No listings are currently available in ZIP code ${zip}.`
                : `No listings match "${listingInput.value.trim()}".`;
    });
}
