import http from 'k6/http';
import {getStationCode} from "./station_helper.js";

const ciCode = "12345678";

export function getCiCode(id) {
	return ciCode + ('000'+id).slice(-3);
}

export function getCreditorInstitutions(rootUrl, params) {
	const url = `${rootUrl}/creditorinstitutions?limit=50&page=0`
	return http.get(url, params);
}

export function createCreditorInstitution(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions`
	const payload = {
		"creditor_institution_code": getCiCode(id),
		"enabled": true,
		"business_name": "Comune di Lorem Ipsum",
		"address": {
			"location": "Via delle vie 3",
			"city": "Rome",
			"zip_code": "00187",
			"country_code": "RM",
			"tax_domicile": "00111"
		},
		"psp_payment": true,
		"reporting_ftp": false,
		"reporting_zip": true
	}

	return http.post(url, JSON.stringify(payload), params);
}

export function getCreditorInstitution(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}`
	return http.get(url, params);
}

export function updateCreditorInstitution(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}`
	const payload = {
		"creditor_institution_code": getCiCode(id),
		"enabled": true,
		"business_name": "Comune di Lorem Ipsum",
		"address": {
			"location": "Via delle vie 3",
			"city": "Rome",
			"zip_code": "00187",
			"country_code": "RM",
			"tax_domicile": "00111"
		},
		"psp_payment": true,
		"reporting_ftp": false,
		"reporting_zip": true
	}

	return http.put(url, JSON.stringify(payload), params);
}

export function deleteCreditorInstitution(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}`

	return http.del(url, params);
}

export function getEncodings(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/encodings`
	return http.get(url, params);
}

export function createEncodings(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/encodings`
	const payload = {
		"code_type": "QR_CODE",
		"encoding_code": getCiCode(id)
	}
	return http.post(url, JSON.stringify(payload), params);
}

export function deleteEncodings(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/encodings/${getCiCode(id)}`

	return http.del(url, params);
}

export function getIbans(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/ibans`
	return http.get(url, params);
}

export function getStationsRelationship(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/stations`
	return http.get(url, params);
}

export function createStationRelationship(rootUrl, params, id, stationId) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/stations`
	const payload = {
		"station_code": getStationCode(stationId),
		"aux_digit": 3,
		"application_code": 1,
		"segregation_code": 1,
		"mod4": false,
		"broadcast": false
	}
	return http.post(url, JSON.stringify(payload), params);
}

export function updateStationRelationship(rootUrl, params, id, stationId) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/stations/${getStationCode(stationId)}`
	const payload = {
		"station_code": getStationCode(stationId),
		"aux_digit": 3,
		"application_code": 1,
		"segregation_code": 1,
		"mod4": false,
		"broadcast": false
	}
	return http.put(url, JSON.stringify(payload), params);
}

export function deleteStationRelationship(rootUrl, params, id, stationId) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/stations/${getStationCode(stationId)}`

	return http.del(url, params);
}
