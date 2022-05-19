import http from 'k6/http';

export function getCounterPartTables(rootUrl, params) {
	const url = `${rootUrl}/counterparttables?limit=50&page=0`
	return http.get(url, params);
}

export function createCounterPartTable(rootUrl, params, id, binFile) {
	const url = `${rootUrl}/counterparttables`

	const payload = {
		file: http.file(binFile, `counterpart_tables_${id}.xml`),
	};

	return http.post(url, payload);
}

export function getCounterPartTable(rootUrl, params, id, ciId) {
	const url = `${rootUrl}/counterparttables/${ciId}_counterpart_tables?creditorinstitutioncode=${ciId}`

	return http.get(url, params);
}

export function deleteCounterPartTable(rootUrl, params, id, ciId) {
	const url = `${rootUrl}/counterparttables/${ciId}_counterpart_tables?creditorinstitutioncode=${ciId}`

	return http.del(url, params);
}
