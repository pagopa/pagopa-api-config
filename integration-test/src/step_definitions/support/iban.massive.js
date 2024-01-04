const fs = require("fs");
const assert = require("assert");
const {Given, When, Then, BeforeAll} = require('@cucumber/cucumber')
const {createMassiveIbansByCsv} = require("./logic/ibanLogic")

let body;
let responseToCheck;

// preventive cancellation to avoid dirty cases
BeforeAll(async function() {
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

// check status code
Then('the client receives the status code {int} for the requested operation', (statusCode) => {
    assert.strictEqual(responseToCheck.status, statusCode);
});