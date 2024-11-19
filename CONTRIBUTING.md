# Contributing

We welcome contributions and are really glad about your involvement in the project.

## Reporting Feedback

Use GitHub’s issues feature to report problems with and request enhancements to the functionality or documentation.

## Contributing Code

Code contributions are highly appreciated. Bug fixes and small enhancements can be handed in as pull-requests. For larger changes and features, please open a GitHub issue with your proposal first so the solution can be discussed.

### Code Checks

The following checks must succeed before code can be merged:

- Java Compile
- Java Test
- Checkstyle
- PMD
- CPD
- Spotbugs
- OakPAL
- Prettier
- TypeScript compile
- ESLint
- Playwright

The `./gradlew check` command executes them all. To reformat code, use `./gradlew prettierFormat`.

The playwright tests also include visual regression tests. The reference images only exist for chromium on linux. All other variations have been deemed too flaky. If you are not on linux (or WSL), you can use docker to run the tests (check the current version of playwright in `package.json` and adjust `PLAYWRIGHT_VERSION` accordingly):

```bash
PLAYWRIGHT_VERSION=v1.49.0
./gradlew frontendBuild
docker run -v .:/app --workdir /app -it --rm mcr.microsoft.com/playwright:$PLAYWRIGHT_VERSION-noble npm run test:playwright
```

Exclude the playwright task from `./gradlew check` by passing `-x playwright`.

### Working on the GUI editor

To run the editor GUI independently from AEM (and with mocked responses), the watch mode can be used:

1. Install the NPM dependencies using `./gradlew npmInstall` (`npm ci` would also work but the Gradle task is preferred because it will use Gradle’s up-to-date checking mechanism).
2. Run `npm run watch` to start both the development server and the JS bundler (parcel) in watch + hot reload mode.

### Commit Guidelines

All your commits must follow the [conventional commit message format](https://www.conventionalcommits.org/en/v1.0.0/#summary).

Valid scopes for this project are:

- `editor`: changes to the script editor GUI
- `runner`: changes to the script execution logic
- `meta`: changes to the build process, deployment, packaging or the project setup

Commits that fall into none of these scopes or change aspects in more than one of them should not specify a scope.

Ideally, each commit should should represent a working and buildable state but still only contain an atomic change compared to its parent. Please rewrite history accordingly before opening a PR.

## License

By contributing code or documentation, you agree to have your contribution licensed under [our MIT-style license](./LICENSE).
