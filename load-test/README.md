### How To

Launch one of the following command according to environment from the current directory `load-test`.

_Note_: configure environment through `*.environment.json` file.

*Where retrieve info:*
- tenantId: [Azure AD](https://portal.azure.com/#blade/Microsoft_AAD_IAM/ActiveDirectoryMenuBlade/Overview)
- clientId: [api-config-fe](https://portal.azure.com/?l=en.en-us#blade/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/Overview/appId/a24c0fa2-cb2f-4d05-a5cf-d71ceef3dbc7/isMSAApp/)
- clientSecret: [api-config-fe secret using JWT token](https://portal.azure.com/?l=en.en-us#blade/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/Credentials/appId/a24c0fa2-cb2f-4d05-a5cf-d71ceef3dbc7/isMSAApp/)
- resource: [api-config](https://portal.azure.com/?l=en.en-us#blade/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/Overview/appId/1169c238-4423-436f-a019-e3f631c7bb5d/isMSAApp/)

##### K6 Scripts
###### Creditor Institution domain
- `creditor_institutions.js`: to test creditor institution section
- `station.js`: to test station section
- `ci_broker.js`: to test creditor institution broker section
- `ica.js`: to test ica section
- `counterpart_tables.js`: to test counterpart tables section

#### Example usage:
##### Local
- based on iterations

 `k6 run --iterations 2 --vus 2 --env VARS=local.environment.json <script>`
 
- based on duration

 `k6 run --duration 1m --vus 2 --env VARS=local.environment.json <script>`
 
 
##### Dev

 `k6 run --iterations 2 --vus 2 --env VARS=dev.environment.json <script>`
