import { devices, PlaywrightTestConfig } from '@playwright/test';
import { getPort } from 'get-port-please';

const outputDir = '../build/reports/playwright';

async function loadConfig(): Promise<PlaywrightTestConfig> {
	const port = await getPort({ random: true });

	/**
	 * See https://playwright.dev/docs/test-configuration.
	 */
	return {
		testDir: '../src/test/playwright',
		/* Run tests in files in parallel */
		fullyParallel: true,
		/* Fail the build on CI if you accidentally left test.only in the source code. */
		forbidOnly: !!process.env.CI,
		/* Retry on CI only */
		retries: process.env.CI ? 2 : 0,
		/* Opt out of parallel tests on CI. */
		workers: process.env.CI ? 1 : undefined,
		/* Reporter to use. See https://playwright.dev/docs/test-reporters */
		reporter: [['html', { open: process.env.CI ? 'never' : 'on-failure', outputFolder: outputDir }]],
		/* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
		use: {
			/* Base URL to use in actions like `await page.goto('/')`. */
			// baseURL: 'http://127.0.0.1:3000',

			/* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
			trace: 'on-first-retry',
		},

		outputDir,

		/* Configure projects for major browsers */
		projects: [
			{
				name: 'chromium',
				use: { ...devices['Desktop Chrome'] },
			},

			{
				name: 'firefox',
				use: { ...devices['Desktop Firefox'] },
			},

			{
				name: 'webkit',
				use: { ...devices['Desktop Safari'] },
			},

			/* Test against mobile viewports. */
			// {
			//   name: 'Mobile Chrome',
			//   use: { ...devices['Pixel 5'] },
			// },
			// {
			//   name: 'Mobile Safari',
			//   use: { ...devices['iPhone 12'] },
			// },

			/* Test against branded browsers. */
			// {
			//   name: 'Microsoft Edge',
			//   use: { ...devices['Desktop Edge'], channel: 'msedge' },
			// },
			// {
			//   name: 'Google Chrome',
			//   use: { ...devices['Desktop Chrome'], channel: 'chrome' },
			// },
		],

		/* Run your local dev server before starting the tests */
		webServer: {
			command: `npm run watch:serve -- --listen=${port}`,
			port,
			reuseExistingServer: !process.env.CI,
		},
	};
}

export default loadConfig();