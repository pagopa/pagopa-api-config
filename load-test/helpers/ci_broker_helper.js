import http from 'k6/http';

const brokerCode = "BROKER";

export function getBrokerCode(id) {
	return brokerCode + ('000'+id).slice(-3);
}

export function getBrokers(rootUrl, params) {
	const url = `${rootUrl}/brokers?limit=50&page=0`
	return http.get(url, params);
}

export function createBroker(rootUrl, params, id) {
	const url = `${rootUrl}/brokers`
	const payload = {
		"broker_code": `${getBrokerCode(id)}`,
		"enabled": true,
		"description": "Regione Lazio",
		"extended_fault_bean": false
	}

	return http.post(url, JSON.stringify(payload), params);
}

export function getBroker(rootUrl, params, id) {
	const url = `${rootUrl}/brokers/${getBrokerCode(id)}`

	return http.get(url, params);
}

export function updateBroker(rootUrl, params, id) {
	const url = `${rootUrl}/brokers/${getBrokerCode(id)}`
	const payload = {
		"broker_code": `${getBrokerCode(id)}`,
		"enabled": true,
		"description": "Regione Lazio",
		"extended_fault_bean": false
	}

	return http.put(url, JSON.stringify(payload), params);
}

export function deleteBroker(rootUrl, params, id) {
	const url = `${rootUrl}/brokers/${getBrokerCode(id)}`

	return http.del(url, params);
}
