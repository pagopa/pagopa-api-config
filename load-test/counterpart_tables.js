// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getCounterPartTables,
	getCounterPartTable,
	createCounterPartTable,
	deleteCounterPartTable,
} from "./helpers/counterpart_tables_helper.js";
import {
	getCiCode,
	createCreditorInstitution,
	deleteCreditorInstitution
} from "./helpers/creditor_institutions_helper.js";


// read configuration
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}/${vars.basePath}`;

// open function is only available in the init stage
const binFile = open('./resources/counterpart_tables.xml');

export function setup() {
	// 2. setup code (once)
	// The setup code runs, setting up the test environment (optional) and generating data
	// used to reuse code for the same VU

	// precondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly
}

function precondition(params, id) {
	// pattern '[0-9]{11}' for type 'stCF'
	const tempId = `9${id}`;

	// remove counterpart tables if it already exists
	let response = deleteCounterPartTable(rootUrl, params, id, getCiCode(tempId));
	let key = `initial step for counterpart tables ${getCiCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// Create creditor institution
	response = createCreditorInstitution(rootUrl, params, tempId);
	check(response, {
		'createCreditorInstitution': (r) => r.status === 201 || r.status === 409,
	});
}

function postcondition(params, id) {
	// pattern '[0-9]{11}' for type 'stCF'
	const tempId = `9${id}`;

	let response = deleteCreditorInstitution(rootUrl, params, tempId);
	let key = `final step for counterpart tables ${getCiCode(tempId)}`;
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

	precondition(params, __VU);

	// pattern '[0-9]{11}' for type 'stCF'
	const tempId = `9${__VU}`;

	// Get counter part tables
	let response = getCounterPartTables(rootUrl, params);
	check(response, {
		'getCounterPartTables': (r) => r.status === 200,
	});

	// Create counter part table
	const multipart_params = {
		headers: {
			'Content-Type': 'multipart/form-data',
			'Authorization': `Bearer ${token}`
		}
	}
	const replacedFile = binFile.replace(/\{creditor_institution}/g, getCiCode(tempId));
	response = createCounterPartTable(rootUrl, multipart_params, __VU, replacedFile);
	check(response, {
		'createCounterPartTable': (r) => r.status === 201,
	});

	// Get counter part table
	response = getCounterPartTable(rootUrl, params, __VU, getCiCode(tempId));
	check(response, {
		'getCounterPartTable': (r) => r.status === 200,
	});

	// Delete counter part table
	response = deleteCounterPartTable(rootUrl, params, __VU, getCiCode(tempId));
	check(response, {
		'deleteCounterPartTable': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
