import http from 'k6/http';

/**
 * Authenticate using OAuth against Azure Active Directory
 * @function
 * @param  {string} tenantId - Directory ID in Azure
 * @param  {string} clientId - Application ID in Azure
 * @param  {string} clientSecret - Can be obtained from https://docs.microsoft.com/en-us/azure/storage/common/storage-auth-aad-app#create-a-client-secret
 * @param {string} resource - Resource ID
 */
export function getJWTToken(tenantId, clientId, clientSecret, resource) {
	let url;
	const requestBody = {
		client_id: clientId,
		client_secret: clientSecret
	};

	url = `https://login.microsoftonline.com/${tenantId}/oauth2/token`;
	requestBody['grant_type'] = 'client_credentials';
	requestBody['resource'] = resource;

	const response = http.post(url, requestBody);
	return response.json().access_token;
}
