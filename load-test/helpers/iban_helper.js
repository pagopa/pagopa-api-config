import http from 'k6/http';

const ciCode = "12345678";
const ibanCodePrefix = "IBANC0222211111000000000";

export function getCiCode(id) {
	return ciCode + ('000'+id).slice(-3);
}

export function getIBANCode(id) {
	return ibanCodePrefix + ('000'+id).slice(-3);
}


export function createIBAN(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/ibans`
	const payload = {
		"description": "Riscossione Canone Unico",
		"iban": `${getIBANCode(id)}`,
		"is_active": true,
		"labels": [
		  {
			"description": "DESCRIPTION-TEST",
			"name": "CUP"
		  }
		],
		"validity_date": "2024-01-01T00:00:00.001Z"
	}

	return http.post(url, JSON.stringify(payload), params);
}

export function getIBAN(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/ibans/enhanced?label=CUP`
	return http.get(url, params);
}

export function updateIBAN(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/ibans/${getIBANCode(id)}`
	const payload =  {
		"description": "Riscossione Canone Unico - Edit",
		"iban": `${getIBANCode(id)}`,
		"is_active": false,
		"labels": [
		  {
			"description": "DESCRIPTION-TEST",
			"name": "CUP"
		  }
		],
		"validity_date": "2025-01-01T00:00:00.001Z"
	}

	return http.put(url, JSON.stringify(payload), params);
}

export function deleteIBAN(rootUrl, params, id) {
	const url = `${rootUrl}/creditorinstitutions/${getCiCode(id)}/ibans/${getIBANCode(id)}`

	return http.del(url, params);
}
