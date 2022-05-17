// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions
import http from 'k6/http';

import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";
import {
	getCiCode,
	getCreditorInstitutions,
	createCreditorInstitution,
	getCreditorInstitution,
	updateCreditorInstitution,
	deleteCreditorInstitution,
	getEncodings,
	createEncodings,
	deleteEncodings,
	getIbans
} from "./helpers/creditor_institutions_helper.js";

// read configuration
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
	const token = vars.env === "local" ? "-" : getJWTToken(vars.tenantId, vars.clientId, vars.clientSecret, vars.resource);

	const params = {
		headers: {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${token}`
		},
	};

	let response = deleteCreditorInstitution(rootUrl, params, __VU);
	const key = `initial step for ${getCiCode(__VU)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
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

	// Get creditor institutions
	let response = getCreditorInstitutions(rootUrl, params);
	check(response, {
		'getCreditorInstitutions': (r) => r.status === 200,
	});

	sleep(0.5)

	// Create creditor institution
	response = createCreditorInstitution(rootUrl, params, __VU);
	check(response, {
		'createCreditorInstitution': (r) => r.status === 201,
	});

	// Get creditor institution
	response = getCreditorInstitution(rootUrl, params, __VU);
	check(response, {
		'getCreditorInstitution': (r) => r.status === 200,
	});

	// Update creditor institution
	response = updateCreditorInstitution(rootUrl, params, __VU);
	check(response, {
		'updateCreditorInstitution': (r) => r.status === 200,
	});

	// Create encodings
	response = createEncodings(rootUrl, params, __VU);
	check(response, {
		'createEncodings': (r) => r.status === 201,
	});

	// Get encodings
	response = getEncodings(rootUrl, params, __VU);
	check(response, {
		'getEncodings': (r) => r.status === 200,
	});

	// Delete encodings
	response = deleteEncodings(rootUrl, params, __VU);
	check(response, {
		'deleteEncodings': (r) => r.status === 200,
	});

	// Get ibans
	response = getIbans(rootUrl, params, __VU);
	check(response, {
		'getIbans': (r) => r.status === 200,
	});

	// Delete creditor institution
	response = deleteCreditorInstitution(rootUrl, params, __VU);
	check(response, {
		'deleteCreditorInstitution': (r) => r.status === 200,
	});
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.
}
