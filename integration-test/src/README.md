# Integration Test with Cucumber

## Technology Stack

- [cucumber js](https://github.com/cucumber/cucumber-js)
- NodeJS v14.17.6

## How to start

- install dependencies: `yarn install`
- run tests: `yarn test`

if all right you should see something like that :

```sh
15 scenarios (15 passed)
65 steps (65 passed)
0m09.409s (executing steps: 0m09.349s)
┌──────────────────────────────────────────────────────────────────────────┐
│ View your Cucumber Report at:                                            │
│ https://reports.cucumber.io/reports/16ebc4c0-cab6-41f6-9355-f894f9a9601d │
│                                                                          │
│ This report will self-destruct in 24h.                                   │
│ Keep reports forever: https://reports.cucumber.io/profile                │
└──────────────────────────────────────────────────────────────────────────┘
```

Click on reporter link to view details .

### Debug

To run a single _feature_ or single _Scenario_ typing

Ex. single _features_ `organizations.feature`

```sh
npx cucumber-js -r step_definitions features/<filename>.feature
```

### Note

Remember to start the Backend before start the tests.

You can configure the host in `./config/.env.local` file.

