// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getCdis,
	getCdi,
	createCdi,
	deleteCdi,
} from "./helpers/cdis_helper.js";
import {
	getPspCode,
	createPaymentServiceProvider,
	deletePaymentServiceProvider,
	createPaymentServiceProvidersChannel,
	deletePaymentServiceProvidersChannel
} from "./helpers/psps_helper.js";
import {
	createChannel,
	deleteChannel,
	createPaymentType,
	deletePaymentType,
	getChannelCode
} from "./helpers/channels_helper.js";
import {
	getBrokerPspCode,
	createBrokerPsp,
	deleteBrokerPsp
} from "./helpers/psp_broker_helper.js";


// read configuration
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}/${vars.basePath}`;

// open function is only available in the init stage
const binFile = open('./resources/cdi.xml');

export function setup() {
	// 2. setup code (once)
	// The setup code runs, setting up the test environment (optional) and generating data
	// used to reuse code for the same VU

	// precondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly
}

function precondition(params, id) {
	// pattern [0-9A-Z]{6,14}_[0-9]{2}-[0-9]{2}-[0-9]{4}
	const tempId = `CDI${id}`;
	// tempId will be like 12345678901DI1_00-00-0000

	// remove counterpart tables if it already exists
	let response = deleteCdi(rootUrl, params, id, getPspCode(tempId));
	let key = `initial step for cdi ${getPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// create a broker in order to create the following channel
	response = createBrokerPsp(rootUrl, params, tempId);
	key = `initial step for channel 1/3 ${getChannelCode(tempId)} / ${getBrokerPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// create a channel in order to create relationship with psp
	response = createChannel(rootUrl, params, tempId, tempId);
	key = `initial step for channel 2/3 ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// create payment type
	response = createPaymentType(rootUrl, params, tempId);
	key = `initial step for channel 3/3 ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// Create psp
	response = createPaymentServiceProvider(rootUrl, params, tempId);
	key = `initial step for psp-channel 1/2 ${getPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// Create channel relationship
	response = createPaymentServiceProvidersChannel(rootUrl, params, tempId, tempId);
	key = `initial step for psp-channel 2/2 ${getPspCode(tempId)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});
}

function postcondition(params, id) {
	const tempId = `CDI${id}`;

	// delete relationship
	let response = deletePaymentServiceProvidersChannel(rootUrl, params, tempId, tempId);
	let key = `final step for psp-channel 1/2 ${getPspCode(tempId)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deletePaymentServiceProvider(rootUrl, params, tempId);
	key = `final step for psp-channel 2/2 ${getPspCode(tempId)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deletePaymentType(rootUrl, params, tempId);
	key = `final step for channel 1/3 ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deleteChannel(rootUrl, params, tempId, tempId);
	key = `final step for channel 2/3 ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deleteBrokerPsp(rootUrl, params, tempId);
	key = `final step for channel 3/3 ${getChannelCode(tempId)} / ${getBrokerPspCode(tempId)}`;
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

	const tempId = `CDI${__VU}`;

	// Get cdis
	let response = getCdis(rootUrl, params);
	check(response, {
		'getCdis': (r) => r.status === 200,
	});

	// Create cdi
	const multipart_params = {
		headers: {
			'Content-Type': 'multipart/form-data',
			'Authorization': `Bearer ${token}`
		}
	}
	const replacedFile = binFile.replace(/\{psp}/g, getPspCode(tempId))
			.replace(/\{brokerpsp}/g, getBrokerPspCode(tempId))
			.replace(/\{channel}/g, getChannelCode(tempId));
	response = createCdi(rootUrl, multipart_params, __VU, replacedFile);
	check(response, {
		'createCdi': (r) => r.status === 201,
	});

	// Get cdi
	response = getCdi(rootUrl, params, __VU, getPspCode(tempId));
	check(response, {
		'getCdi': (r) => r.status === 200,
	});

	// Delete cdi
	response = deleteCdi(rootUrl, params, __VU, getPspCode(tempId));
	check(response, {
		'deleteCdi': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
