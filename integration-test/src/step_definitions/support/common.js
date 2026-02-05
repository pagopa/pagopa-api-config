const axios = require("axios");

axios.defaults.headers.common['Content-Type'] = 'application/json'
axios.defaults.headers.common['Ocp-Apim-Subscription-Key'] = process.env.SUBKEY // for all requests
if (process.env.canary) {
  axios.defaults.headers.common['X-Canary'] = 'canary' // for all requests
}

function logRequest(method, url, body, config) {
  console.log('\n========== REQUEST ==========');
  console.log(`METHOD: ${method}`);
  console.log(`URL: ${url}`);
  console.log('HEADERS:', {
    ...axios.defaults.headers.common,
    ...(config?.headers || {})
  });
  if (body) {
    const bodyStr = typeof body === 'string' ? body : JSON.stringify(body, null, 2);
    if (bodyStr.length > 500) {
      console.log('BODY (truncated):', bodyStr.substring(0, 500) + '...');
    } else {
      console.log('BODY:', bodyStr);
    }
  }
  console.log('=============================\n');
}

function logResponse(method, url, res, isError = false) {
  console.log('\n========== RESPONSE ==========');
  console.log(`METHOD: ${method}`);
  console.log(`URL: ${url}`);
  console.log(`STATUS: ${res?.status || 'UNDEFINED'}`);
  console.log(`STATUS TEXT: ${res?.statusText || 'UNDEFINED'}`);
  if (isError) {
    console.log('ERROR: Request failed');
  }
  if (res?.headers) {
    console.log('RESPONSE HEADERS:', res.headers);
  }
  if (res?.data) {
    const dataStr = typeof res.data === 'string' ? res.data : JSON.stringify(res.data, null, 2);
    if (dataStr.length > 1000) {
      console.log('RESPONSE DATA (truncated):', dataStr.substring(0, 1000) + '...');
    } else {
      console.log('RESPONSE DATA:', dataStr);
    }
  } else {
    console.log('RESPONSE DATA: UNDEFINED/NULL');
  }
  console.log('==============================\n');
}

function get(url) {
  return axios.get(url)
    .then(res => {
      if (res.status >= 400) {
        logRequest('GET', url);
        logResponse('GET', url, res);
      }
      return res;
    })
    .catch(error => {
      logRequest('GET', url);
      logResponse('GET', url, error.response, true);
      return error.response;
    });
}

function post(url, body, config) {
  return axios.post(url, body, config)
    .then(res => {
      if (res.status >= 400) {
        logRequest('POST', url, body, config);
        logResponse('POST', url, res);
      }
      return res;
    })
    .catch(error => {
      logRequest('POST', url, body, config);
      logResponse('POST', url, error.response, true);
      return error.response;
    });
}

function put(url, body) {
  return axios.put(url, body)
    .then(res => {
      if (res.status >= 400) {
        logRequest('PUT', url, body);
        logResponse('PUT', url, res);
      }
      return res;
    })
    .catch(error => {
      logRequest('PUT', url, body);
      logResponse('PUT', url, error.response, true);
      return error.response;
    });
}

function del(url) {
  return axios.delete(url)
    .then(res => {
      if (res.status >= 400) {
        logRequest('DELETE', url);
        logResponse('DELETE', url, res);
      }
      return res;
    })
    .catch(error => {
      logRequest('DELETE', url);
      logResponse('DELETE', url, error.response, true);
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

module.exports = { get, post, put, del, call }
