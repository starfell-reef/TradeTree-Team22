
# Requirements – Trade Tree

**Project Name:** Trade Tree
**Team:** Galileo Black - Jacob Edwards and Galileo Black
**Course:** CSC 340
**Version:** 1.0
**Date:** 2026-09-18

---

## 1. Overview
**Vision.** Trade Tree is for people who want to exchange creative goods and services without relying on traditional payment. The website will help users find relevant listings, communicate with trading partners, complete trades, and build trust through reviews.

**Glossary.** Terms used in the project
- **Listing:** A post describing an item or service that a user is offering and what they would like in return.
- **Trade:** An exchange of goods or services agreed upon by two users.
- **Marketplace:** The collection of active listings that users can browse and filter.
- **Infraction:** A recorded violation of the website's rules that may result in account restrictions.

**Primary Users / Roles.**
- **Customer:** A user who browses listings and contacts another user to request a trade.
- **Provider:** A user who creates listings for goods or services and responds to trade requests.
- **SysAdmin:** An administrator who moderates listings, reviews, reports, and user access.

**Scope (this semester).**
- Account registration, profile management, and role-based access for users and administrators.
- Creating, viewing, filtering, and moderating listings for tradeable goods and services.
- Messaging between users about listings, reviews after completed trades, and reporting users or listings.

**Out of scope (deferred).**
- Integrated payments, shipping, and automatic valuation of goods or services.
- Native mobile applications and integrations with external marketplaces or social networks.
- Automated moderation using machine learning.

> This document is **requirements‑level** and solution‑neutral; design decisions (UI layouts, API endpoints, schemas) are documented separately.

---

## 2. Functional Requirements (User Stories)

### 2.1 User Stories
- **US‑1 — Register profile**  
  _Story:_ As a user, I want to create an account, so that I may interact with the website
  _Acceptance:_
  ```gherkin
  Scenario: Register account
    Given I have no account
    When  I enter my credentials
    Then  I should be registered and logged in 
  ```

- **US‑2 — Create a listing**  
  _Story:_ As a user, I want to make a listing, so that I can trade
  _Acceptance:_
  ```gherkin
  Scenario: Create a listing
    Given I have something to trade
    When  I press the Create a Listing button
    Then  I should be able to create a listing
    And   I should be able to view the listing
  ```

- **US‑3 — Leaving reviews**  
  _Story:_ As a user, I want to leave a review, so that others can see
  _Acceptance:_
  ```gherkin
  Scenario: Leave a review
    Given I have traded something recently
    When  I press the Leave a Review button
    Then  I should be able to leave a review with the person I traded with
  ```

- **US‑4 — Messaging posters**  
  _Story:_ As a user, I want to message posters, so that we can trade 
  _Acceptance:_
  ```gherkin
  Scenario: Messaging users
    Given A trade listing has been posted
    When  I press the Message button
    Then  I can message the poster about the listing
  ```

- **US‑5 — Editing account**  
  _Story:_ As a user, I want to edit account, so that I can update my profile
  _Acceptance:_
  ```gherkin
  Scenario: Editing account
    Given I am logged into my account
    When  I press the edit profile button
    Then  I can edit my account
  ```

- **US‑6 — Reporting others**  
  _Story:_ As a user, I wan tto report other users, so that they get an infraction
  _Acceptance:_
  ```gherkin
  Scenario: Report needed
    Given I am logged into my account
    When  I press the report button
    Then  A report is sent to admins regarding the user reported
  ```


### 2.2 SysAdmin Stories
- **US-20 — Reviewing reports**  
  _Story:_ As an admin, I wan tto review reports, so that the site is safe
  _Acceptance:_
  ```gherkin
  Scenario: Report requires review
    Given I am logged into my account
    When  A report is generated
    Then  I should get a notification
    And   I can look at the report
  ```

- **US-21 — Editing listings**  
  _Story:_ As an admin, I want to edit listings, so that no scams are listed
  _Acceptance:_
  ```gherkin
  Scenario: Listing needs editing
    Given A listing is available
    When  I press the Edit the Listing button
    Then  I should be able to delete or edit the listing
  ```

- **US-22 — Removing users**  
  _Story:_ As an admin, I want to remove users, so that bad users are removed
  _Acceptance:_
  ```gherkin
  Scenario: A user needs removing
    Given A user has account reports
    When  I press the account punishments button
    Then  I have the option to delete the account
  ```

---

## 3. Non‑Functional Requirements
- **Performance:** The marketplace and listing pages should load within 3 seconds under normal course-project usage. Typical user actions should receive a response within 2 seconds.
- **Availability/Reliability:** The application should preserve successfully submitted account, listing, message, review, and report data across sessions. Errors should provide a clear message without silently losing submitted data.
- **Security/Privacy:** Passwords must not be stored or displayed in plain text. Users may access only their own profile information and conversations, while administrative actions must require an administrator role.
- **Usability:** A new user should be able to register, find a listing, and send a message without assistance. Forms must label required fields and identify validation errors in plain language.

---

## 4. Assumptions, Constraints, and Policies
- Users are responsible for accurately describing their listings and for arranging safe trade details with other users.
- The project is developed as a CSC 340 course project and must use the team's approved repository and development tools.
- Users must not use the service for illegal goods, harassment, fraud, or content that violates the team's terms of service.
- Administrators may remove listings, restrict accounts, or remove reviews that violate the service rules.
- The system depends on users having internet access and a modern web browser.

---

## 5. Milestones (course‑aligned)
- **M1 Requirements** — this file + stories opened as issues. 
- **M2 High‑fidelity prototype** — core customer/provider flows fully interactive. 
- **M3 Design** — architecture, schema, API outline. 
- **M4 Backend API** — key endpoints + tests. 
- **M5 Increment** — ≥2 use cases end‑to‑end. 
- **M6 Final** — complete system & documentation. 

---

## 6. Change Management
- Stories are living artifacts; changes are tracked via repository issues and linked pull requests.  
- Major changes should update this SRS.
