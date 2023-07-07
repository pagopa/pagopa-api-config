const {post, del, put, get} = require("../common");

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


module.exports = {
    createNewIban,
    deleteIban,
    updateIban,
    getIbanEnhanced
}