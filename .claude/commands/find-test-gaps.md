---
description: Analyze test coverage gaps and interactively generate missing tests
allowed-tools: Read, Bash, Write, Edit, Agent, AskUserQuestion
---

Analyze this Spring Boot project for test coverage gaps, then offer to generate the missing tests.

## Step 1 — Discover existing tests

Read every file under `src/test/` and build a list of what is already covered:
- Which production classes have a corresponding test class
- Which methods / behaviors are exercised

## Step 2 — Discover all production code

Read every file under `src/main/java/` and catalog:
- Domain records and their constructors / methods
- Application services and all code paths (happy path + each exception branch)
- Input port commands / output port interfaces
- Web adapters: controllers, DTOs, validators, exception handlers
- Persistence adapters and JPA repositories

## Step 3 — Identify gaps

For each untested class or behavior, produce one line in this format:

| # | Class / Behavior | What to test | Suggested type |
|---|---|---|---|

Suggested types: **Unit**, **@WebMvcTest**, **@DataJpaTest**, **@SpringBootTest E2E**

Only list gaps that have real value — skip empty interfaces, Lombok-generated boilerplate, and trivial getters.

## Step 4 — Present findings to user

Print the gap table clearly. Briefly explain any non-obvious entries.

## Step 5 — Ask the user which tests to generate

Use the `AskUserQuestion` tool with a single multi-select question. Each option should be one gap from the table, labeled as `#N — ClassName: what to test (Type)`. Always include:
- An option **"All of the above"** as the first choice
- An option **"None — I'll decide later"** as the last choice

The user can also type free-form input if they want something not listed.

## Step 6 — Generate selected tests

For each selected gap:
1. Read the relevant production source file(s) if not already read
2. Write a new test file (or add to an existing one if appropriate) following the conventions already established in this project:
   - JUnit 5 + AssertJ + Mockito
   - `@MockitoBean` (not `@MockBean`) in Spring slice tests
   - `@Testcontainers` + `@ServiceConnection` + `PostgreSQLContainer` for any test that needs a real DB
   - `@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)` on `@DataJpaTest` classes
   - `@WithMockUser` + `.with(csrf())` on `@WebMvcTest` classes
   - Test classes in the **same package** as the class under test (required for package-private classes)
3. Confirm each file written with its path

If the user selected "None", acknowledge and stop.