const {Given} = require('@cucumber/cucumber')
const {get} = require("../common");
const assert = require("assert");

const app_host = process.env.APP_HOST;

Given('ApiConfig running', async () => {
    const response = await get(app_host + `/info`);
    assert.strictEqual(response.status, 200);
});