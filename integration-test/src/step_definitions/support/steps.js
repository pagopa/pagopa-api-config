const {Given, When, Then} = require('@cucumber/cucumber')
const assert = require("assert");
const {createNewIban, deleteIban, updateIban, getIbanEnhanced} = require("./logic/ibanLogic")
const {buildIbanCreate, buildIbanUpdate} = require("./builder/buildIban")
const {buildCDI} = require("./builder/buildCDI")
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

When('the client creates the Iban {string}', async (iban) => {
  body = buildIbanCreate(iban);
  responseToCheck = await createNewIban(creditorInstitution, body);    
});

When('the client {string} Iban {string}', async (method, iban) => {
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

When('the client gets the Ibans for an EC', async () => {
  responseToCheck = await getIbanEnhanced(creditorInstitution);
});

Then('the client {string} the iban {string}', async (method, iban) => {
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
});

Then('the response {string} is equal to {string}', (field, description) =>{
  assert.strictEqual(responseToCheck.data[field], description);
});

When('The client creates a CDI with an IdentificativoFlusso valued as {string} and an IdentificativoPSP valued as {string}', async (identificativoFlusso, identificativoPSP) => {
  body = buildCDI(identificativoFlusso, identificativoPSP);
  responseToCheck = await createCDI(body);    
});


When('The client deletes the created CDI with an IdentificativoFlusso valued as {string} and an IdentificativoPSP valued as {string}', async (identificativoFlusso, identificativoPSP) => {
	responseToCheck = await deleteCDI(identificativoFlusso, identificativoPSP);   
});	

