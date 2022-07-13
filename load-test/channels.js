// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

import {getJWTToken} from "./helpers/authentication.js";

import {
	getChannelCode,
	getChannels,
	getChannel,
	createChannel,
	deleteChannel,
	updateChannel,
	getChannelPaymentServiceProviders,
	getPaymentTypes,
	createPaymentType,
	deletePaymentType
} from "./helpers/channels_helper.js";
import {
	createBrokerPsp,
	deleteBrokerPsp,
	getBrokerPspCode} from "./helpers/psp_broker_helper.js";
import {
	getPspCode,
	createPaymentServiceProvider,
	deletePaymentServiceProvider,
	createPaymentServiceProvidersChannel,
	deletePaymentServiceProvidersChannel
} from "./helpers/psps_helper.js";


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
	const tempId = `CHANNEL${id}`;

	let response = deleteChannel(rootUrl, params, id);
	let key = `initial step for channel ${getChannelCode(id)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// create a broker in order to create the following channel
	response = createBrokerPsp(rootUrl, params, tempId);
	key = `initial step for channel-broker ${getChannelCode(id)} / ${getBrokerPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

	// create a psp in order to create the channel relationship
	response = createPaymentServiceProvider(rootUrl, params, tempId);
	key = `initial step for channel-psp ${getChannelCode(id)} / ${getPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 201 || r.status === 409,
	});

}

function postcondition(params, id) {
	const tempId = `CHANNEL${id}`;

	let response = deleteBrokerPsp(rootUrl, params, tempId);
	let key = `final step for channel-brokerpsp ${getChannelCode(id)} / ${getBrokerPspCode(tempId)}`;
	check(response, {
		[key]: (r) => r.status === 200 || r.status === 404,
	});

	// delete psp
	response = deletePaymentServiceProvider(rootUrl, params, tempId);
	key = `final step for channel-psp ${getChannelCode(id)} / ${getPspCode(tempId)}`;
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

	const tempId = `CHANNEL${__VU}`;

	// Get channel list
	let response = getChannels(rootUrl, params);
	check(response, {
		'getChannels': (r) => r.status === 200,
	});

	// Create channel
	response = createChannel(rootUrl, params, __VU, tempId);
	check(response, {
		'createChannel': (r) => r.status === 201,
	});

	sleep(0.5)

	// Get channel
	response = getChannel(rootUrl, params, __VU);
	check(response, {
		'getChannel': (r) => r.status === 200,
	});

	// Update channel
	response = updateChannel(rootUrl, params, __VU, tempId);
	check(response, {
		'updateChannel': (r) => r.status === 200,
	});

	// Create payment type
	response = createPaymentType(rootUrl, params, __VU);
	check(response, {
		'createPaymentType': (r) => r.status === 201,
	});

	// Get payment types
	response = getPaymentTypes(rootUrl, params, __VU);
	check(response, {
		'getPaymentTypes': (r) => r.status === 200,
	});

	// Create channel relationship
	response = createPaymentServiceProvidersChannel(rootUrl, params, tempId, __VU);
	check(response, {
		'createPaymentServiceProvidersChannel': (r) => r.status === 201
	});

	// Get psp relationship
	response = getChannelPaymentServiceProviders(rootUrl, params, __VU);
	check(response, {
		'getChannelPaymentServiceProviders': (r) => r.status === 200,
	});

	// Create payment type
	response = deletePaymentType(rootUrl, params, __VU);
	check(response, {
		'deletePaymentType': (r) => r.status === 200,
	});

	// Delete channel relationship
	response = deletePaymentServiceProvidersChannel(rootUrl, params, tempId, __VU);
	check(response, {
		'deletePaymentServiceProvidersChannel': (r) => r.status === 200,
	});

	// Delete channel
	response = deleteChannel(rootUrl, params, __VU);
	check(response, {
		'deleteChannel': (r) => r.status === 200,
	});

	postcondition(params, __VU);
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
