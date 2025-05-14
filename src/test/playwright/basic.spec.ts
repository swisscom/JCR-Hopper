import { test, expect } from '@playwright/test';

test('render', async ({ page }) => {
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
});
