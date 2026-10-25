# egovframe-template-simple-backend: project context

## Role, scope, and public entry points

`README.md` presents the simple-homepage backend separated from its React frontend. Public
surfaces include REST endpoints, `*ApiController.java` controllers, JWT authentication,
authorization rules, board/file APIs, schedules, and member/admin functionality. `Docs/` explains
Java configuration conversion; `DATABASE/` supplies database assets. Preserve response, error,
authentication/cookie, upload/storage, and persistence contracts.
Use the actual `SecurityConfig.java` filter chain when assessing access behavior: README route
summaries are contextual claims, not proof that every matching route is public.

## Build and verification evidence

- `pom.xml` is version 5.0.2, inherits the eGovFrame boot parent 5.0.1, and targets Java 17.
  README describes Spring Boot 3.5.6 / Spring Framework 6.2.11 / Servlet 6.0.
- `.github/workflows/maven.yml` targets `main` and runs
  `mvn -B package --file pom.xml -DexcludedGroups=browser` on JDK 17.
- `.github/workflows/fork-ci.yml` targets `work` and runs
  `mvn -B -ntp package --file pom.xml -DexcludedGroups=browser` on JDK 17.
  Browser-tagged tests are excluded. Packaging does not establish browser/UI behavior or
  successful execution of tests not discovered by the configured test runner.
- README documents `mvn spring-boot:run`, default HTTP port 8080, and
  `/swagger-ui/index.html`. A frontend is maintained separately; it is not part of adoption.
- README explains environment-provided signing/encryption keys and file-store/profile overrides.
  Keep real key values out of source/cards. Runtime tests need suitable DB/configuration and a
  writable file store; inspect the affected test setup rather than assuming services exist.

## Contribution and submission baseline

`AGENTS.md` defines mirror `main`, fork `work`, issue evidence, fork review and CI requirements.
Actual parent: `eGovFramework/egovframe-template-simple-backend`. Fetch its default branch and
record the exact SHA; preserve existing submission branches/PRs. The shell helper's denylist
now includes `.specify`/`.omp` per local instructions, but it still starts from `work`.
Do not use it for the new workflow: create a separate upstream-based worktree so neither
work-only files nor tooling commits enter the upstream submission. Adoption creates no PR;
existing application review requirements apply to later contributions.

## Exploration limits

No `.codegraph/` index was present. Evidence comes from targeted README, AGENTS, POM, CI,
and helper reads. No application execution or dependency update was performed.


## Maintenance process

Follow [constitution.md](constitution.md) for the domain-card, assessment, planning, verification,
and submission gates. Commands are installed under `.omp/commands/`; read them as agent
instructions. No application build or test was run for tool adoption. Scaffold checks establish
tool installation only, not application correctness.
