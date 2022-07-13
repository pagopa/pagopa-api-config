import http from 'k6/http';

const brokerCode = "PSPBROKER";

export function getBrokerPspCode(id) {
	return brokerCode + ('000'+id).slice(-3);
}

export function getBrokersPsp(rootUrl, params) {
	const url = `${rootUrl}/brokerspsp?limit=50&page=0`
	return http.get(url, params);
}

export function createBrokerPsp(rootUrl, params, id) {
	const url = `${rootUrl}/brokerspsp`
	const payload = {
		"broker_psp_code": `${getBrokerPspCode(id)}`,
		"description": "Postecom",
		"enabled": true,
		"extended_fault_bean": false
	}

	return http.post(url, JSON.stringify(payload), params);
}

export function getBrokerPsp(rootUrl, params, id) {
	const url = `${rootUrl}/brokerspsp/${getBrokerPspCode(id)}`

	return http.get(url, params);
}

export function updateBrokerPsp(rootUrl, params, id) {
	const url = `${rootUrl}/brokerspsp/${getBrokerPspCode(id)}`
	const payload = {
		"broker_psp_code": `${getBrokerPspCode(id)}`,
		"description": "Postecom",
		"enabled": true,
		"extended_fault_bean": false
	}

	return http.put(url, JSON.stringify(payload), params);
}

export function deleteBrokerPsp(rootUrl, params, id) {
	const url = `${rootUrl}/brokerspsp/${getBrokerPspCode(id)}`

	return http.del(url, params);
}
