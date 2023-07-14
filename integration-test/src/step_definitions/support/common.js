const axios = require("axios");

axios.defaults.headers.common['Content-Type'] = 'application/json'
axios.defaults.headers.common['Ocp-Apim-Subscription-Key'] = process.env.subkey // for all requests
if (process.env.canary) {
  axios.defaults.headers.common['X-Canary'] = 'canary' // for all requests
}

function get(url) {
  return axios.get(url)
  .then(res => {
    return res;
  })
  .catch(error => {
    return error.response;
  });
}

function post(url, body) {
  return axios.post(url, body)
  .then(res => {
    return res;
  })
  .catch(error => {
    return error.response;
  });
}

function put(url, body) {
  return axios.put(url, body)
  .then(res => {
    return res;
  })
  .catch(error => {
    return error.response;
  });
}

function del(url) {
  return axios.delete(url)
  .then(res => {
    return res;
  })
  .catch(error => {
    return error.response;
  });
}

function call(method, url, body) {
  if (method === 'GET') {
    return get(url)
  }
  if (method === 'POST') {
    return post(url, body)
  }
  if (method === 'PUT') {
    return put(url, body)
  }
  if (method === 'DELETE') {
    return del(url)
  }

}

module.exports = {get, post, put, del, call}
