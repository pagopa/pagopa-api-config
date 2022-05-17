### How To

#### Example usage:
From the current directory `load-test` launch one of the following command according to environment.

**Local**

 `k6 run --vus 2 --env VARS=local.environment.json creditor_institutions.js`
 
**Dev**

 `k6 run --vus 2 --env VARS=dev.environment.json creditor_institutions.js`
