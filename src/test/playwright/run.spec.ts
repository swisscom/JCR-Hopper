import { test, expect, Request } from '@playwright/test';

test('run simple', async ({ page }) => {
	await page.goto('/');

	await page.getByRole('button', { name: 'Add', exact: true }).click();
	await page.getByRole('option', { name: 'Query JCR' }).click();
	await page.getByLabel('Log Level: INFO').click();
	await page.getByRole('option', { name: 'ERROR' }).click();

	let runScriptRequest: Promise<Request> | Request = page.waitForRequest('/mock/mock-response.jsonl');
	await page.getByRole('button', { name: 'Run' }).click();
	runScriptRequest = await runScriptRequest;

	expect(runScriptRequest.method()).toBe('POST');
	expect(runScriptRequest.postData()).toContain(
		'{"logLevel":"error","hops":[{"type":"nodeQuery","query":"SELECT * FROM [cq:Page] AS page","queryType":"JCR-SQL2","hops":[]}],"parameters":[]}',
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
    - text: /info JCR Hopper script finished after \\d+ms warn Not saving changes as dry run is enabled error Script execution aborted with exception/
    - button "!!"
	`);

	await expect(output).toHaveScreenshot({
		mask: [output.getByRole('heading', { level: 3 })],
	});
});

test('run with arguments', async ({ page }) => {
	await page.goto('/');

	await page.getByRole('button', { name: 'Add', exact: true }).click();
	await page.getByRole('option', { name: 'Resolve Specific Node' }).click();
	await page.getByRole('button', { name: 'add circle' }).click();
	await page.getByRole('option', { name: 'Get Child Nodes' }).click();
	await page.getByRole('heading', { name: 'Get Child Nodes Matching' }).click();

	const namePattern = page.getByLabel('Name Pattern:');
	await namePattern.click();
	await namePattern.press('ControlOrMeta+a');
	await namePattern.fill('');
	await namePattern.press('Tab');

	await page.getByRole('button', { name: 'add', exact: true }).click();
	const paramName = page.getByPlaceholder('Name');
	await paramName.click();
	await paramName.press('ControlOrMeta+a');
	await paramName.fill('myparam');
	await paramName.press('Tab');

	const argValue = page.getByLabel('myparam:');
	await argValue.click();
	await argValue.fill('myvalue');
	await argValue.press('Tab');
	await page.getByRole('button', { name: 'Run' }).click();

	let runScriptRequest: Promise<Request> | Request = page.waitForRequest('/mock/mock-response.jsonl');
	await page.getByRole('button', { name: 'Run' }).click();
	runScriptRequest = await runScriptRequest;

	expect(runScriptRequest.method()).toBe('POST');
	expect(runScriptRequest.postData()).toContain(
		'{"logLevel":"info","hops":[{"type":"resolveNode","conflict":"ignore","name":"child-name","hops":[{"type":"childNodes","namePattern":"","hops":[]}]}],"parameters":[{"name":"myparam","defaultValue":"","type":"text","evaluation":"STRING"}]}',
	);
	expect(runScriptRequest.postData()).toContain('myparam');
	expect(runScriptRequest.postData()).toContain('myvalue');
});
