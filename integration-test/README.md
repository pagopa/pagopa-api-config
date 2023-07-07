# Integration Tests

👀 Integration tests are in `integration-test/src/` folder. See there for more information.

## How run on Docker 🐳

To run the integration tests on docker, you can run from this directory the script:

``` shell
sh ./run_integration_test.sh <local|dev|uat|prod> <sub-key>
```

ℹ️ _Note_: for **PagoPa ACR** is **required** the login `az acr login -n <acr-name>`

If you use dev, uat or prod **you test the images on Azure ACR**

---
💻 If you want to test your local branch,

``` shell
sh ./run_integration_test.sh local SUBSCRIPTION-KEY
```

