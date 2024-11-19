import { devices, PlaywrightTestConfig } from '@playwright/test';
import { getPort } from 'get-port-please';

const outputDir = '../build/playwright';

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
		reporter: [
			['html', { open: 'never', outputFolder: '../build/reports/playwright' }],
			['junit', { outputFile: `${outputDir}/TEST-playwright.xml` }],
		],
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
		],

		expect: {
			toHaveScreenshot: {
				maxDiffPixelRatio: 0.05,
				maxDiffPixels: 100,
			},
		},

		/* Run your local dev server before starting the tests */
		webServer: {
			command: `npm run watch:serve -- --listen=${port}`,
			port,
			reuseExistingServer: !process.env.CI,
		},
	};
}

export default loadConfig();
