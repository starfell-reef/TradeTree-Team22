
# Requirements – Trade Tree

**Project Name:** Trade Tree
**Team:** Galileo Black - Customer, Name and role
**Course:** CSC 340
**Version:** 1.0
**Date:** 2026-09-18

---

## 1. Overview
**Vision.** One or two sentences: who this is for, the core problem, and the outcome.

**Glossary** Terms used in the project
- **Term 1:** description.
- **Term 2:** description

**Primary Users / Roles.**
- **Customer (e.g., Student/Patient/Pet Owner/etc. )** — 1 line goal statement.
- **Provider (e.g., Teacher/Doctor/Pet Sitter/etc. )** — 1 line goal statement.
- **SysAdmin (optional)** — 1 line goal statement.

**Scope (this semester).**
- <capability 1>
- <capability 2>
- <capability 3>

**Out of scope (deferred).**
- <deferred 1>
- <deferred 2>

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
  ``

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

## 3. Non‑Functional Requirements (make them measurable)
- **Performance:** description 
- **Availability/Reliability:** description
- **Security/Privacy:** description
- **Usability:** description

---

## 4. Assumptions, Constraints, and Policies
- list any rules, policies, assumptions, etc.

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
