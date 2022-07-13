import http from 'k6/http';

function flowIdentifier(pspId) {
	return `${pspId}_00-00-0000`
}

export function getCdis(rootUrl, params) {
	const url = `${rootUrl}/cdis?limit=50&page=0`
	return http.get(url, params);
}

export function createCdi(rootUrl, params, id, binFile) {
	const url = `${rootUrl}/cdis`

	const payload = {
		file: http.file(binFile, `cdis_${id}.xml`),
	};

	return http.post(url, payload);
}

export function getCdi(rootUrl, params, id, pspId) {
	const url = `${rootUrl}/cdis/${flowIdentifier(pspId)}?pspcode=${pspId}`;
	return http.get(url, params);
}

export function deleteCdi(rootUrl, params, id, pspId) {
	const url = `${rootUrl}/cdis/${flowIdentifier(pspId)}?pspcode=${pspId}`;
	return http.del(url, params);
}
