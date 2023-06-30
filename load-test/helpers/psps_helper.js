import http from 'k6/http';
import {getChannelCode} from "./channels_helper.js";


const pspCode = "12345678901";

export function getPspCode(id) {
	return pspCode + ('000' + id).slice(-3);
}

export function getPaymentServiceProviders(rootUrl, params) {
	const url = `${rootUrl}/paymentserviceproviders?limit=50&page=0`
	return http.get(url, params);
}

export function getPaymentServiceProvider(rootUrl, params, id) {
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}`
	return http.get(url, params);
}

export function createPaymentServiceProvider(rootUrl, params, id) {
	const url = `${rootUrl}/paymentserviceproviders`;
	const payload = {
		"psp_code": getPspCode(id),
		"enabled": true,
		"business_name": "PSP Business name",
		"abi": "01600",
		"bic": "BPPIITRRZZZ",
		"stamp": false,
		"agid_psp": false,
		"tax_code": "17103880000"
	};
	return http.post(url, JSON.stringify(payload), params);
}

export function updatePaymentServiceProvider(rootUrl, params, id) {
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}`
	const payload = {
		"psp_code": getPspCode(id),
		"enabled": true,
		"business_name": "PSP Business name",
		"abi": "01600",
		"bic": "BPPIITRRZZZ",
		"stamp": false,
		"agid_psp": false,
		"tax_code": "17103880000"
	};

	return http.put(url, JSON.stringify(payload), params);
}

export function deletePaymentServiceProvider(rootUrl, params, id) {
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}`;

	return http.del(url, params);
}

export function getPaymentServiceProvidersChannels(rootUrl, params, id) {
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}/channels`
	return http.get(url, params);
}

export function createPaymentServiceProvidersChannel(rootUrl, params, id, channelId) {
	channelId = channelId ? channelId : id
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}/channels`
	const payload = {
		"channel_code": getChannelCode(channelId),
		"payment_types": [
			"BP"
		]
	}
	return http.post(url, JSON.stringify(payload), params);
}

export function deletePaymentServiceProvidersChannel(rootUrl, params, id, channelId) {
	channelId = channelId ? channelId : id
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}/channels/${getChannelCode(channelId)}`
	return http.del(url, params);
}

export function updatePaymentServiceProvidersChannel(rootUrl, params, id, channelId) {
	channelId = channelId ? channelId : id
	const url = `${rootUrl}/paymentserviceproviders/${getPspCode(id)}/channels/${getChannelCode(channelId)}`
	const payload = {
		"channel_code": getChannelCode(channelId),
		"payment_types": [
			"BP"
		]
	}
	return http.put(url, JSON.stringify(payload), params);
}



