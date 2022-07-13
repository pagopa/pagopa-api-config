// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getBrokerPspCode,
	getBrokersPsp,
	getBrokerPsp,
	createBrokerPsp,
	deleteBrokerPsp,
	updateBrokerPsp,
} from "./helpers/psp_broker_helper.js";

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

	// remove broker psp if already exists
	let response = deleteBrokerPsp(rootUrl, params, id);
	let key = `initial step for channel ${getBrokerPspCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});
}

function postcondition(params, id) {
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

	// Get brokers
	let response = getBrokersPsp(rootUrl, params);
	check(response, {
		'getBrokersPsp': (r) => r.status === 200,
	});

	// Create broker
	response = createBrokerPsp(rootUrl, params, __VU);
	check(response, {
		'createBrokerPsp': (r) => r.status === 201,
	});

	// Get broker
	response = getBrokerPsp(rootUrl, params, __VU);
	check(response, {
		'getBrokerPsp': (r) => r.status === 200,
	});

	// Update broker
	response = updateBrokerPsp(rootUrl, params, __VU);
	check(response, {
		'updateBrokerPsp': (r) => r.status === 200,
	});

	// Delete broker
	response = deleteBrokerPsp(rootUrl, params, __VU);
	check(response, {
		'deleteBrokerPsp': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
