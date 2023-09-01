const {post, del} = require("../common");
const FormData = require('form-data');

const app_host = process.env.APP_HOST;

async function createCDI(body) {
	let formData = new FormData();
    formData.append('file', body, "cdi.xml");
	const headersConfig = {headers:{'Content-Type': `multipart/form-data; boundary=${formData.getBoundary()}`}};
	
    return post(app_host + `/cdis`, formData, headersConfig)
}

async function deleteCDI(idCdi, pspCode) {
    return del(app_host + `/cdis/${idCdi}?pspcode=${pspCode}`)
}

module.exports = {
    createCDI,
    deleteCDI
}