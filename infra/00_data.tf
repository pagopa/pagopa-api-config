data "azuread_application" "apiconfig-fe" {
  display_name = format("pagopa-%s-apiconfig-fe", var.env_short)
}

data "azuread_application" "apiconfig-be" {
  display_name = format("pagopa-%s-apiconfig-be", var.env_short)
}

data "azurerm_api_management_product" "apim_aca_integration_product" {
  product_id          = "aca-integration"
  api_management_name = local.apim.name
  resource_group_name = local.apim.rg
}

data "azurerm_api_management_product" "technical_support_api_product" {
  product_id          = "technical_support_api"
  api_management_name = local.apim.name
  resource_group_name = local.apim.rg
}