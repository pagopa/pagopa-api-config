import http from 'k6/http';
import {getBrokerCode} from "./ci_broker_helper.js";

const stationCode = "STATION";

export function getStationCode(id) {
	return stationCode + ('000'+id).slice(-3);
}

export function createStation(rootUrl, params, id) {
	const url = `${rootUrl}/stations`
	const payload = {
		"station_code": `${getStationCode(id)}`,
		"enabled": true,
		"version": 1,
		"ip": "NodoDeiPagamentiDellaPATest.sia.eu",
		"password": "password",
		"port": 80,
		"redirect_ip": "paygov.collaudo.regione.lazio.it",
		"redirect_path": "nodo-regionale-fesp/paaInviaRispostaPagamento.html",
		"redirect_port": 443,
		"service": "openspcoop/PD/RT6TPDREGVENETO",
		"redirect_protocol": "HTTPS",
		"proxy_enabled": true,
		"proxy_host": "10.101.1.95",
		"proxy_port": 8080,
		"protocol": "HTTP",
		"thread_number": 2,
		"timeout_a": 15,
		"timeout_b": 30,
		"timeout_c": 120,
		"flag_online": true,
		"broker_code": `${getBrokerCode(id)}`
	}

	return http.post(url, JSON.stringify(payload), params);
}

export function deleteStation(rootUrl, params, id) {
	const url = `${rootUrl}/stations/${getStationCode(id)}`

	return http.del(url, params);
}
