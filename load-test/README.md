### How To

#### Example usage:
From the current directory `load-test` launch one of the following command according to environment.

_Note_: configure environment through `*.environment.json` file.

##### Local
- based on iterations

 `k6 run --iterations 2 --vus 2 --env VARS=local.environment.json creditor_institutions.js`
 
 `k6 run --iterations 2 --vus 2 --env VARS=local.environment.json station.js`
 
- based on duration

 `k6 run --duration 1m --vus 2 --env VARS=local.environment.json creditor_institutions.js`
 
 and so on.

 
##### Dev

 `k6 run --iterations 2 --vus 2 --env VARS=dev.environment.json creditor_institutions.js`
