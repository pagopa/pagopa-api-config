import http from 'k6/http';

// const brokerCode = "BROKER";

// export function getBrokerCode(id) {
// 	return brokerCode + ('000'+id).slice(-3);
// }

export function getIcas(rootUrl, params) {
	const url = `${rootUrl}/icas?limit=50&page=0`
	return http.get(url, params);
}

export function createIca(rootUrl, params, id, binFile) {
	const url = `${rootUrl}/icas`

	const payload = {
		field: '',
		file: http.file(binFile, `ica_${id}.xml`),
	};

	return http.post(url, payload);
}

export function getIca(rootUrl, params, id, ciId) {
	const url = `${rootUrl}/icas/${ciId}_ica?creditorinstitutioncode=${ciId}`

	return http.get(url, params);
}

export function deleteIca(rootUrl, params, id, ciId) {
	const url = `${rootUrl}/icas/${ciId}_ica?creditorinstitutioncode=${ciId}`

	return http.del(url, params);
}

export function checkXSD(rootUrl, params, id, binFile) {
	const url = `${rootUrl}/icas/xsd`

	const payload = {
		field: '',
		file: http.file(binFile, `ica_${id}.xml`),
	};

	return http.post(url, payload);
}
