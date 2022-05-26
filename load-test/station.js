// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getStationCode,
	getStations,
	createStation,
	getStation,
	updateStation,
	deleteStation,
	getStationCreditorInstitutions,
	getStationCreditorInstitution
} from "./helpers/station_helper.js";

import {
	getBrokerCode,
	createBroker,
	deleteBroker
} from "./helpers/ci_broker_helper.js";
import {
	getCiCode,
	createCreditorInstitution,
	deleteCreditorInstitution,
	createStationRelationship,
	deleteStationRelationship
} from "./helpers/creditor_institutions_helper.js";


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
	const tempId = `STATION${id}`;

	// remove the station if it already exists
	let response = deleteStation(rootUrl, params, id);
	let key = `initial step for station ${getStationCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// create a broker in order to create the station
	response = createBroker(rootUrl, params, tempId);
	key = `initial step for station-broker ${getStationCode(id)} / ${getBrokerCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// Create creditor institution
	response = createCreditorInstitution(rootUrl, params, tempId);
	key = `initial step for station-ec ${getStationCode(id)} / ${getCiCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

}

function postcondition(params, id) {
	const tempId = `STATION${id}`;

	// remove creditor institution and broker used in the test
	let response = deleteCreditorInstitution(rootUrl, params, tempId);
	let key = `final step for station-ec ${getStationCode(tempId)} / ${getCiCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deleteBroker(rootUrl, params, tempId);
	key = `final step for station-broker ${getStationCode(id)} / ${getBrokerCode(tempId)}`;
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

	const tempId = `STATION${__VU}`;

	precondition(params, __VU);

	// Get stations
	let response = getStations(rootUrl, params);
	check(response, {
		'getStations': (r) => r.status === 200,
	});

	sleep(0.5)

	// Create station
	response = createStation(rootUrl, params, __VU, getBrokerCode(tempId));
	check(response, {
		'createStation': (r) => r.status === 201,
	});

	// Get station
	response = getStation(rootUrl, params, __VU);
	check(response, {
		'getStation': (r) => r.status === 200,
	});

	// Update station
	response = updateStation(rootUrl, params, __VU, getBrokerCode(tempId));
	check(response, {
		'updateStation': (r) => r.status === 200,
	});

	// Create station relationship
	response = createStationRelationship(rootUrl, params, tempId, __VU);
	check(response, {
		'createStationRelationship': (r) => r.status === 201 || r.status === 404,
	});

	sleep(0.5)

	// Get creditor institutions
	response = getStationCreditorInstitutions(rootUrl, params, __VU);
	check(response, {
		'getStationCreditorInstitutions': (r) => r.status === 200,
	});

	// Get creditor institution
	response = getStationCreditorInstitution(rootUrl, params, __VU, getCiCode(tempId));
	check(response, {
		'getStationCreditorInstitution': (r) => r.status === 200,
	});

	// Delete station relationship
	response = deleteStationRelationship(rootUrl, params, tempId, __VU);
	check(response, {
		'deleteStationRelationship': (r) => r.status === 200,
	});

	// Delete station
	response = deleteStation(rootUrl, params, __VU);
	check(response, {
		'deleteStation': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
