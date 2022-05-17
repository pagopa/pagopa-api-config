// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getIcas,
	createIca,
	getIca,
	deleteIca,
	checkXSD
} from "./helpers/ica_helper.js";
import {
	getCiCode,
	createCreditorInstitution,
	deleteCreditorInstitution,
	createEncodings,
	deleteEncodings
} from "./helpers/creditor_institutions_helper.js";


// read configuration
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}/${vars.basePath}`;

// open function is only available in the init stage
const binFile = open('./resources/ica.xml');

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

	// remove ica if it already exists
	let response = deleteIca(rootUrl, params, id, getCiCode(tempId));
	let key = `initial step for ica ${getCiCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// Create creditor institution
	response = createCreditorInstitution(rootUrl, params, tempId);
	check(response, {
		'createCreditorInstitution': (r) => r.status === 201 || r.status === 409,
	});

	response = createEncodings(rootUrl, params, tempId);
	check(response, {
		'createEncodings': (r) => r.status === 201 || r.status === 409,
	});
}

function postcondition(params, id) {
	// pattern '[0-9]{11}' for type 'stCF'
	const tempId = `9${id}`;

	// remove creditor institution and encodings used in the test
	let response = deleteEncodings(rootUrl, params, tempId);
	let key = `final step for ica 1/2 ${getCiCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deleteCreditorInstitution(rootUrl, params, tempId);
	key = `final step for ica 2/2 ${getCiCode(tempId)}`;
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

	// Get icas
	let response = getIcas(rootUrl, params);
	console.error("ICAS", JSON.stringify(response.json()))
	check(response, {
		'getIcas': (r) => r.status === 200,
	});

	// Create ica
	const multipart_params = {
		headers: {
			'Content-Type': 'multipart/form-data',
			'Authorization': `Bearer ${token}`
		}
	}
	const replacedFile = binFile.replace(/\{creditor_institution}/g, getCiCode(tempId));
	response = createIca(rootUrl, multipart_params, __VU, replacedFile);
	check(response, {
		'createIca': (r) => r.status === 201,
	});

	// Get ica
	response = getIca(rootUrl, params, __VU, getCiCode(tempId));
	check(response, {
		'getIca': (r) => r.status === 200,
	});

	// Delete ica
	response = deleteIca(rootUrl, params, __VU, getCiCode(tempId));
	check(response, {
		'deleteIca': (r) => r.status === 200,
	});

	response = checkXSD(rootUrl, multipart_params, __VU, replacedFile);
	check(response, {
		'checkXSD': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
