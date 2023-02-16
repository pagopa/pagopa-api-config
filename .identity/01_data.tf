data "azurerm_storage_account" "tfstate_app" {
  name                = "pagopainfraterraform${var.env}"
  resource_group_name = "io-infra-rg"
}

data "azurerm_resource_group" "dashboards" {
  name = "dashboards"
}

data "azurerm_api_management_product" "product" {
  product_id          = "afm-calculator"
  api_management_name = "${local.product}-apim"
  resource_group_name = "${local.product}-api-rg"
}
