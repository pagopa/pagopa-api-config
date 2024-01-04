const {post, del, put, get} = require("../common");
const FormData = require('form-data');

const app_host = process.env.APP_HOST;

async function createNewIban(creditorInstitution, body) {
    return post(app_host + `/creditorinstitutions/${creditorInstitution}/ibans`, body)
}

async function deleteIban(creditorInstitution, iban) {
    return del(app_host + `/creditorinstitutions/${creditorInstitution}/ibans/${iban}`)
}

async function updateIban(creditorInstitution, iban, body) {
    return put(app_host + `/creditorinstitutions/${creditorInstitution}/ibans/${iban}`, body)
}

async function getIbanEnhanced(creditorInstitution) {
    return get(app_host + `/creditorinstitutions/${creditorInstitution}/ibans/enhanced`)
}

async function createMassiveIbansByCsv(body) {
	let formData = new FormData();
	formData.append('file', body, "data.csv");
	const headersConfig = {headers:{'Content-Type': `multipart/form-data; boundary=${formData.getBoundary()}`}};
    return post(app_host + `/creditorinstitutions/ibans/csv`, formData, headersConfig);
}


module.exports = {
    createNewIban,
    deleteIban,
    updateIban,
    getIbanEnhanced,
    createMassiveIbansByCsv
}