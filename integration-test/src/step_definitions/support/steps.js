const {Given, When, Then} = require('@cucumber/cucumber')
const assert = require("assert");
const {createNewIban, deleteIban, updateIban} = require("./logic/ibanLogic")
const {buildIbanCreate, buildIbanUpdate} = require("./builder/buildIban")
const {get} = require("./common");

const app_host = process.env.APP_HOST;

let creditorInstitution = process.env.CREDITOR_INSTITUTION
let body;
let responseToCheck;

Given('ApiConfig running', async () => {
  const response = await get(app_host + `/info`, {
    headers: {
        "Ocp-Apim-Subscription-Key": process.env.subkey
    }
  });
  assert.strictEqual(response.status, 200);
});

When('the client creates the Iban', async () => {
      body = buildIbanCreate();
      responseToCheck = await createNewIban(creditorInstitution, body);
    });

Then('the {string} of the iban', async (method) => {
  let iban = body['iban']
  switch (method) {
    case 'delete':
      responseToCheck = await deleteIban(creditorInstitution, iban);
      break;
    case 'update':
      body = buildIbanUpdate();
      responseToCheck = await updateIban(creditorInstitution, iban, body);
      break;
    case 'get':
      break;
  }
});

Then('the client receives status code {int}', (statusCode) =>{
  assert.strictEqual(responseToCheck.status, statusCode);
})

Then('the response {string} is equal to {string}', (field, description) =>{
  assert.strictEqual(responseToCheck.data[field], description);
})
