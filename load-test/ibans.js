// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions
import http from 'k6/http';

import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";
import {
	createIBAN,
	getIBAN,
	updateIBAN,
	deleteIBAN,
} from "./helpers/iban_helper.js";
import {
	getCiCode,
	createCreditorInstitution,
	getCreditorInstitution,
	deleteCreditorInstitution,
} from "./helpers/creditor_institutions_helper.js";
import {
} from "./helpers/iban_helper.js";


// read configuration
// note: SharedArray can currently only be constructed inside init code
// according to https://k6.io/docs/javascript-api/k6-data/sharedarray
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}/${vars.basePath}`;

export function setup() {
	// 2. setup code (once)
	// The setup code runs, setting up the test environment (optional) and generating data
	// used to reuse code for the same VU

	// precondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly
}

function precondition(params, id) {

	// remove the creditor institution if it already exists
	let response = deleteCreditorInstitution(rootUrl, params, id);
	check(response, {
		'deleteCreditorInstitution': (r) => r.status === 200 || r.status === 404,
	});

  	// Create creditor institution
  	response = createCreditorInstitution(rootUrl, params, __VU);
  	check(response, {
  		'createCreditorInstitution': (r) => r.status === 201,
  	});
}

function postcondition(params, id) {
	// remove the creditor institution if it already exists
	let response = deleteCreditorInstitution(rootUrl, params, id);
	check(response, {
		'deleteCreditorInstitution': (r) => r.status === 200 || r.status === 404,
	});
}

export default function (data) {
	// 3. VU code (once per iteration, as many times as the test options require)
	// VU code runs in the default() function, running for as long and as many times as the options define.
	const token = vars.env === "local" ? "-" : getJWTToken(vars.tenantId, vars.clientId, vars.clientSecret, vars.resource);

	const params = {
		headers: {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${token}`
		},
	};

	precondition(params, __VU);

  // Create IBAN
  let response = createIBAN(rootUrl, params, __VU);
	check(response, {
		'createIBAN': (r) => r.status === 201,
	});

  	// Update IBAN
	response = updateIBAN(rootUrl, params, __VU);
	check(response, {
		'updateIBAN': (r) => r.status === 200,
	});

  	// Get IBAN for CI, found something
	response = getIBAN(rootUrl, params, __VU);
	check(response, {
		'getIBAN': (r) => r.status === 200,
	});

  	// Delete IBAN
	response = deleteIBAN(rootUrl, params, __VU);
	check(response, {
		'deleteIBAN': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
