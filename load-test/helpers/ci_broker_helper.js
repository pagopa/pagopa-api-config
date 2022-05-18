import http from 'k6/http';

const brokerCode = "BROKER";

export function getBrokerCode(id) {
	return brokerCode + ('000'+id).slice(-3);
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

export function deleteBroker(rootUrl, params, id) {
	const url = `${rootUrl}/brokers/${getBrokerCode(id)}`

	return http.del(url, params);
}
