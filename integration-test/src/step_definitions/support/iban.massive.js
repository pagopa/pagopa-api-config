const fs = require("fs");
const assert = require("assert");
const {When, Then, BeforeAll, AfterAll} = require('@cucumber/cucumber')
const {createMassiveIbansByCsv} = require("./logic/ibanLogic")

let body;
let responseToCheck;

// preventive cancellation to avoid dirty cases
BeforeAll(async function() {
        body = fs.readFileSync("step_definitions/support/resources/delete.csv", "utf-8");
		await createMassiveIbansByCsv(body);
});

// cleans the database after the integration tests
AfterAll(async function() {
        body = fs.readFileSync("step_definitions/support/resources/delete.csv", "utf-8");
		await createMassiveIbansByCsv(body);
});


// massive create ibans
When('the client loads a csv file with ibans to create', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/insert.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// massive update ibans 
When('the client loads a csv file with ibans to update', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/update.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// massive delete ibans 
When('the client loads a csv file with ibans to delete', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/delete.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// mixed behavior
When('the client loads a csv file with mixed behavior ibans', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/mix.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD insert: two entries on the same PA for the same IBAN
When('the client loads a bad csv file with two entries on the same PA for the same IBAN', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_insert.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD insert: two insert operations for the same POSTAL IBAN in two different PAs
When('the client loads a bad csv file with two insert operations for the same POSTAL IBAN in two different PAs', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_insert_postal_iban.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD insert: invalid IBAN entry
When('the client loads a bad csv file with an invalid IBAN entry', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_iban.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD insert: csv file is incorrectly loaded twice in a row
When('the client loads the same CSV file used previously', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/insert.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD delete: PA-Iban relationship does not exist
When('the client loads a bad csv file with PA - Iban relationship that does not exist for deletion', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_delete.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD update: PA-Iban relationship does not exist
When('the client loads a bad csv file with PA - Iban relationship that does not exist for the update', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_update.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// BAD csv file: not well formed
When('the client loads not well formed csv file', async () => {
	    body = fs.readFileSync("step_definitions/support/resources/BAD_format.csv", "utf-8");
		responseToCheck = await createMassiveIbansByCsv(body);
});

// check status code
Then('the client receives the status code {int} for the requested operation', (statusCode) => {
    assert.strictEqual(responseToCheck.status, statusCode);
});