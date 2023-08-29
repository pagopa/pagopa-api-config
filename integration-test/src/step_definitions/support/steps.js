const {Given, When, Then} = require('@cucumber/cucumber')
const assert = require("assert");
const {createNewIban, deleteIban, updateIban, getIbanEnhanced} = require("./logic/ibanLogic")
const {buildIbanCreate, buildIbanUpdate} = require("./builder/buildIban")
const {buildCDI} = require("./builder/buildCDI")
const {createCDI, deleteCDI} = require("./logic/cdiLogic")
const {get} = require("./common");
const {makeIdMix,makeIdNumber} = require("./utility/helpers")

const app_host = process.env.APP_HOST;

let creditorInstitution = process.env.CREDITOR_INSTITUTION
let body;
let responseToCheck;
// pattern is '[0-9A-Z]{6,14}_[0-9]{2}-[0-9]{2}-[0-9]{4}'
let randomIdFlusso = makeIdMix(14)+"_"+makeIdNumber(2)+"-"+makeIdNumber(2)+"-"+makeIdNumber(4)

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

// entered a specific step's timeout to override the default (to prevent timeout error)
When('the client creates a CDI with a runtime random IdentificativoFlusso and an IdentificativoPSP valued as {string}', {timeout: 20000}, async (identificativoPSP) => {
  body = buildCDI(randomIdFlusso, identificativoPSP);
  responseToCheck = await createCDI(body);    
});

// entered a specific step's timeout to override the default (to prevent timeout error)
When('the client deletes the created CDI with an IdentificativoPSP valued as {string}', {timeout: 20000}, async (identificativoPSP) => {
	responseToCheck = await deleteCDI(randomIdFlusso, identificativoPSP);   
});	

