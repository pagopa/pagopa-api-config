const {post, del} = require("../common");

const app_host = process.env.APP_HOST;

async function createCDI(body) {
    return post(app_host + `/cdis`, body)
}

async function deleteCDI(idCdi, pspCode) {
    return del(app_host + `/cdis/${idCdi}?pspcode=${pspCode}`)
}

module.exports = {
    createCDI,
    deleteCDI
}