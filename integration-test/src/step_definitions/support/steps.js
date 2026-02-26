const {When, Then} = require('@cucumber/cucumber')
const assert = require("assert");
const {createNewIban, deleteIban, updateIban, getCreditorInstitutionsIbans} = require("./logic/ibanLogic")
const {buildIbanCreate, buildIbanUpdate} = require("./builder/buildIban")
const {buildCDI, buildCDIH2} = require("./builder/buildCDI")
const {createCDI, deleteCDI} = require("./logic/cdiLogic")
const {makeIdMix, makeIdNumber} = require("./utility/helpers")
const { setTimeout } = require("timers/promises");

const app_host = process.env.APP_HOST;

let creditorInstitution = process.env.CREDITOR_INSTITUTION
// Use different PSP based on environment
const isUAT = app_host && app_host.includes('uat');
const isLocal = app_host && app_host.includes('localhost');
const defaultPSP = isLocal ? '' : (isUAT ? 'BPPIITRRXXX' : 'BPPIITRRZZZ');
let body;
let responseToCheck;
// pattern is '[0-9A-Z]{6,14}_[0-9]{2}-[0-9]{2}-[0-9]{4}'
let randomIdFlusso = makeIdMix(14) + "_" + makeIdNumber(2) + "-" + makeIdNumber(2) + "-" + makeIdNumber(4)

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
    responseToCheck = await getCreditorInstitutionsIbans(creditorInstitution);
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

Then('the client receives status code {int}', (statusCode) => {
    assert.strictEqual(responseToCheck.status, statusCode);
});

Then('the response {string} is equal to {string}', (field, description) => {
    assert.strictEqual(responseToCheck.data[field], description);
});

// entered a specific step's timeout to override the default (to prevent timeout error)
When('the client creates a CDI with a runtime random IdentificativoFlusso and an IdentificativoPSP valued as {string}', {timeout: 20000}, async (identificativoPSP) => {
    let pspToUse;
    
    if (isLocal) {
        // For local environment, always use buildCDIH2 with the PSP from feature file
        body = buildCDIH2(randomIdFlusso, identificativoPSP);
    } else if (isUAT && (identificativoPSP === 'BPPIITRRZZZ' || identificativoPSP === '')) {
        // For UAT, replace DEV PSP or empty PSP with UAT PSP
        pspToUse = 'BPPIITRRXXX';
        body = buildCDI(randomIdFlusso, pspToUse);
    } else {
        // For DEV or when a specific PSP is provided (like CHARITYNEXI), use it as-is
        pspToUse = identificativoPSP || defaultPSP;
        body = buildCDI(randomIdFlusso, pspToUse);
    }
    
    responseToCheck = await createCDI(body);
});

// entered a specific step's timeout to override the default (to prevent timeout error)
When('the client deletes the created CDI with an IdentificativoPSP valued as {string}', {timeout: 20000}, async (identificativoPSP) => {
    await setTimeout(5000);
    
    let pspToUse;
    
    if (isUAT && (identificativoPSP === 'BPPIITRRZZZ' || identificativoPSP === '')) {
        // For UAT, replace DEV PSP or empty PSP with UAT PSP
        pspToUse = 'BPPIITRRXXX';
    } else {
        // For DEV/Local or when a specific PSP is provided, use it as-is
        pspToUse = identificativoPSP || defaultPSP;
    }
    
    responseToCheck = await deleteCDI(randomIdFlusso, pspToUse);
});	

