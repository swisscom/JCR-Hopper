import { test, expect, Request } from '@playwright/test';

test('renders', async ({ page }) => {
	await page.goto('/');

	await expect(page).toHaveTitle(/JCR Hopper Script Builder/);
	await expect(page).toHaveScreenshot({ fullPage: true });
});

test('add step', async ({ page }) => {
	await page.goto('/');

	await page.getByRole('button', { name: 'Add', exact: true }).click();

	await expect(page).toHaveScreenshot();
	await page.getByRole('option', { name: 'Query JCR' }).click();
	await page.getByLabel('Log Level: INFO').click();
  await page.getByRole('option', { name: 'TRACE' }).click();
	await expect(page).toHaveScreenshot();

	const step = page.locator('.hop-config.nodeQuery');

	await expect(step.locator('summary')).toMatchAriaSnapshot(`
- heading "Query JCR Using SQL2 for SELECT * FROM [cq:Page] AS page" [level=2]
- button "arrow up" [disabled]:
  - img "arrow up"
- button "arrow down" [disabled]:
  - img "arrow down"
- button "duplicate":
  - img "duplicate"
- button "delete":
  - img "delete"
	`);
	await step.getByRole('heading', { name: 'Query JCR Using SQL2 for' }).click();
	await step.getByLabel('Selector Name:').click();
	await step.getByLabel('Selector Name:').fill('page');
	await step.getByLabel('Selector Name:').press('Tab');

	await expect(step).toHaveScreenshot();

	let runScriptRequest: Promise<Request> | Request = page.waitForRequest('/mock/mock-response.jsonl');
	await page.getByRole('button', { name: 'Run' }).click();
	runScriptRequest = await runScriptRequest;

	expect(runScriptRequest.method()).toBe('POST');
	expect(runScriptRequest.postData()).toContain(
		'{"logLevel":"trace","hops":[{"type":"nodeQuery","query":"SELECT * FROM [cq:Page] AS page","queryType":"JCR-SQL2","hops":[],"selectorName":"page"}],"parameters":[]}',
	);

	const output = page.locator('.output');

	await expect(output).toMatchAriaSnapshot(`
  - group:
    - text: ▼
    - heading /✅ \\d+\\/\\d+\\/\\d+, \\d+:\\d+:\\d+/ [level=3]
    - text: "/trace Starting JCR Hopper with 6 parameters, for 4 of which arguments were passed: \\\\[\\\\] debug JCR Hopper script started at \\\\d+ Some plain text output/"
    - img "file csv"
    - text: /Test\\.csv text\\/csv;charset=utf-8 \\d+ bytes/
    - link "Download":
      - button "Download"
    - text: /info JCR Hopper script finished after \\d+[hmsp]+ warn Not saving changes as dry run is enabled error Script execution aborted with exception/
    - button "!!"
	`);

});
