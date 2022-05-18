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
	getIbans,
	getStationsRelationship,
	createStationRelationship,
	updateStationRelationship,
	deleteStationRelationship
} from "./helpers/creditor_institutions_helper.js";

import {
	getStationCode,
	createStation,
	deleteStation
} from "./helpers/station_helper.js";

import {
	getBrokerCode,
	createBroker,
	deleteBroker
} from "./helpers/ci_broker_helper.js";

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

	// precondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly
}

function precondition(params, id) {
	const tempId = `CI${id}`;

	// remove the creditor institution if it already exists
	let response = deleteCreditorInstitution(rootUrl, params, id);
	let key = `initial step for creditor institution ${getCiCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// create a broker in order to create the following station
	response = createBroker(rootUrl, params, tempId);
	key = `initial step for station-broker ${getStationCode(id)} / ${getBrokerCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	sleep(0.5)

	// create a station in order to create relationship with creditor institution
	// id is created with CI to avoid collision with stations script
	response = createStation(rootUrl, params, tempId);
	key = `initial step for ci-station relationship 1/2 ${getCiCode(id)} / ${getStationCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// remove ci-station relationship
	response = deleteStationRelationship(rootUrl, params, id, tempId);
	key = `initial step for ci-station relationship 2/2 ${getCiCode(id)} / ${getStationCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});
}

function postcondition(params, id) {
	const tempId = `CI${id}`;

	// remove station and broker used in the test
	let response = deleteStation(rootUrl, params, tempId);
	let key = `final step for ci-station relationship ${getCiCode(id)} / ${getStationCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// response = deleteBroker(rootUrl, params, tempId);
	// key = `final step for ci-broker ${getCiCode(id)} / ${getBrokerCode(tempId)}`;
	// check(response, {
	// 	[key]: (r) => r.status === 200 || r.status === 404,
	// });
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

	// Create station relationship
	response = createStationRelationship(rootUrl, params, __VU, `CI${__VU}`);
	console.log("STATION", JSON.stringify(response.json()))
	check(response, {
		'createStationRelationship': (r) => r.status === 201,
	});

	// Get stations relationship
	response = getStationsRelationship(rootUrl, params, __VU);
	check(response, {
		'getStationsRelationship': (r) => r.status === 200,
	});

	// Update station relationship
	response = updateStationRelationship(rootUrl, params, __VU, `CI${__VU}`);
	check(response, {
		'updateStationRelationship': (r) => r.status === 200,
	});

	// Delete station relationship
	response = deleteStationRelationship(rootUrl, params, __VU, `CI${__VU}`);
	check(response, {
		'deleteStationRelationship': (r) => r.status === 200,
	});

	// Delete creditor institution
	response = deleteCreditorInstitution(rootUrl, params, __VU);
	check(response, {
		'deleteCreditorInstitution': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
