// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getPspCode,
	getPaymentServiceProviders,
	getPaymentServiceProvider,
	createPaymentServiceProvider,
	updatePaymentServiceProvider,
	deletePaymentServiceProvider,
	getPaymentServiceProvidersChannels,
	createPaymentServiceProvidersChannel,
	deletePaymentServiceProvidersChannel,
	updatePaymentServiceProvidersChannel
} from "./helpers/psps_helper.js";
import {createBrokerPsp, deleteBrokerPsp, getBrokerPspCode} from "./helpers/psp_broker_helper.js";
import {createChannel, deleteChannel, getChannelCode, createPaymentType, deletePaymentType} from "./helpers/channels_helper.js";


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
	const tempId = `PSP${id}`;

	// remove the psp-channel relationship if it already exists
	let response = deletePaymentServiceProvidersChannel(rootUrl, params, id, tempId);
	let key = `initial step for psp 1/2 ${getPspCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// remove the psp if it already exists
	response = deletePaymentServiceProvider(rootUrl, params, id);
	key = `initial step for psp 2/2 ${getPspCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// create a broker in order to create the following channel
	response = createBrokerPsp(rootUrl, params, tempId);
	key = `initial step for channel-broker ${getChannelCode(tempId)} / ${getBrokerPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// create a channel in order to create relationship with psp
	// id is created with PSP to avoid collision with stations script
	response = createChannel(rootUrl, params, tempId);
	key = `initial step for psp-channel relationship 1/2 ${getPspCode(id)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	response = createPaymentType(rootUrl, params, tempId);
	key = `initial step for channel-paymenttype relationship 1/2 ${getPspCode(id)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

}

function postcondition(params, id) {
	const tempId = `PSP${id}`;

	// remove payment type
	let response = deletePaymentType(rootUrl, params, tempId);
	let key = `final step for channel-paymenttype relationship ${getPspCode(id)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// remove channel and broker used in the test
	response = deleteChannel(rootUrl, params, tempId);
	key = `final step for psp-channel relationship ${getPspCode(id)} / ${getChannelCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	response = deleteBrokerPsp(rootUrl, params, tempId);
	key = `final step for channel-brokerpsp ${getPspCode(id)} / ${getBrokerPspCode(tempId)}`;
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

	const tempId = `PSP${__VU}`;

	// Get psp list
	let response = getPaymentServiceProviders(rootUrl, params);
	check(response, {
		'getPaymentServiceProviders': (r) => r.status === 200,
	});

	// Create psp
	response = createPaymentServiceProvider(rootUrl, params, __VU);
	check(response, {
		'createPaymentServiceProvider': (r) => r.status === 201,
	});

	// Get psp
	response = getPaymentServiceProvider(rootUrl, params, __VU);
	check(response, {
		'getPaymentServiceProvider': (r) => r.status === 200,
	});

	// Update psp
	response = updatePaymentServiceProvider(rootUrl, params, __VU);
	check(response, {
		'updatePaymentServiceProvider': (r) => r.status === 200,
	});

	// Create channel relationship
	response = createPaymentServiceProvidersChannel(rootUrl, params, __VU, tempId);
	check(response, {
		'createPaymentServiceProvidersChannel': (r) => r.status === 201
	});

	// Get psp channel list
	response = getPaymentServiceProvidersChannels(rootUrl, params, __VU);
	check(response, {
		'getPaymentServiceProvidersChannels': (r) => r.status === 200,
	});

	// Update channel relationship
	response = updatePaymentServiceProvidersChannel(rootUrl, params, __VU, tempId);
	check(response, {
		'updatePaymentServiceProvidersChannel': (r) => r.status === 200
	});

	// Delete channel relationship
	response = deletePaymentServiceProvidersChannel(rootUrl, params, __VU, tempId);
	check(response, {
		'deletePaymentServiceProvidersChannel': (r) => r.status === 200,
	});

	// Delete psp
	response = deletePaymentServiceProvider(rootUrl, params, __VU);
	check(response, {
		'deletePaymentServiceProvider': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
