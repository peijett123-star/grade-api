# Lab 5 Task 3

Task 3a and Task 3b are implemented in this branch with AI assistance. This does not claim independent student mastery or completion of the team's other lab tasks.

## Behavior

`Application` → `GetAverageGradeUseCase.getAverageGrade(course)` → `GradeDataBase.getMyTeam()` → `getGrades(username)` for each member → filter grades by course → divide the sum by the number of matching grades.

Members with no matching grade are excluded from the denominator. No matching grades or an empty team returns `0.0`. For example, grades 85 and 81 for the requested course produce `83.0`; grades for other courses do not affect the result.

`MongoGradeDataBase.getMyTeam()` sends `GET /team` with the existing environment token, parses `team.name` and `team.members`, and closes the response. HTTP errors, API errors, malformed JSON and I/O failures are propagated instead of being treated as successful empty teams. The package-private constructor injects the client and a synthetic token for offline tests.

## Validation

Run `mvn -o test` after Maven dependencies are available locally. On Java 11, all 10 tests passed: the two unchanged course tests, three average-grade edge cases and five mocked HTTP/JSON tests. No external API users, teams or grades were created, and no real token was used in tests.

Removed `-XX:+EnableDynamicAgentLoading` from Surefire because the starter flag is unsupported on Java 11, which the project declares as its target. Mockito's tests pass without it on the tested Java 11 runtime.

To check manually with an existing authorized API account, run `app.gui.Application`, open `My Team`, enter the same course identifier used when logging the grades, and click `Get average grade`. Live API verification has not been performed as part of this change.

Task 4's UI and the other team tasks are outside this PR.
