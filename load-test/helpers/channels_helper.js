import http from 'k6/http';
import {getBrokerPspCode} from "./psp_broker_helper.js";

const channelCode = "CHANNEL";

export function getChannelCode(id) {
	return channelCode + id;
}

export function getChannels(rootUrl, params) {
	const url = `${rootUrl}/channels?limit=50&page=0`
	return http.get(url, params);
}

export function getChannel(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}`
	return http.get(url, params);
}

export function createChannel(rootUrl, params, id, brokerId) {
	brokerId = brokerId ? brokerId : id
	const url = `${rootUrl}/channels`;
	const payload = {
		"channel_code": getChannelCode(id),
		"enabled": true,
		"description": "",
		"password": "FakePay",
		"protocol": "HTTPS",
		"ip": "1.1.1.1",
		"port": 443,
		"service": "basepath/services/fake",
		"broker_psp_code": getBrokerPspCode(brokerId),
		"proxy_enabled": true,
		"proxy_host": "2.2.2.2",
		"proxy_port": 8080,
		"thread_number": 2,
		"timeout_a": 15,
		"timeout_b": 30,
		"timeout_c": 120,
		"new_fault_code": true,
		"redirect_protocol": "HTTP",
		"payment_model": "ACTIVATED_AT_PSP",
		"rt_push": true,
		"on_us": false,
		"card_chart": false,
		"recovery": true,
		"digital_stamp_brand": false,
		"flag_io": false,
		"serv_plugin": "idPsp1",
		"agid": true
	}
	return http.post(url, JSON.stringify(payload), params);
}

export function updateChannel(rootUrl, params, id, brokerId) {
	brokerId = brokerId ? brokerId : id
	const url = `${rootUrl}/channels/${getChannelCode(id)}`;
	const payload = {
		"channel_code": getChannelCode(id),
		"enabled": true,
		"description": "",
		"password": "FakePay",
		"protocol": "HTTPS",
		"ip": "1.1.1.1",
		"port": 443,
		"service": "basepath/services/fake",
		"broker_psp_code": getBrokerPspCode(brokerId),
		"proxy_enabled": true,
		"proxy_host": "2.2.2.2",
		"proxy_port": 8080,
		"thread_number": 2,
		"timeout_a": 15,
		"timeout_b": 30,
		"timeout_c": 120,
		"new_fault_code": true,
		"redirect_protocol": "HTTP",
		"payment_model": "ACTIVATED_AT_PSP",
		"rt_push": true,
		"on_us": false,
		"card_chart": false,
		"recovery": true,
		"digital_stamp_brand": false,
		"flag_io": false,
		"serv_plugin": "idPsp1",
		"agid": true
	}
	return http.put(url, JSON.stringify(payload), params);
}

export function deleteChannel(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}`;
	return http.del(url, params);
}

// export function getPaymentServiceProvidersChannels(rootUrl, params, id) {
// 	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}/channels`
// 	return http.get(url, params);
// }

export function getPaymentTypes(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}/paymenttypes`
	return http.get(url, params);
}

export function createPaymentType(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}/paymenttypes`;
	const payload = {
		"payment_types": [
			"BP"
		]
	}
	return http.post(url, JSON.stringify(payload), params);
}

export function deletePaymentType(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}/paymenttypes/BP`;
	return http.del(url, params);
}

export function getChannelPaymentServiceProviders(rootUrl, params, id) {
	const url = `${rootUrl}/channels/${getChannelCode(id)}/paymentserviceproviders`
	return http.get(url, params);
}

