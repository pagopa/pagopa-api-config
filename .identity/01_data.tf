data "azurerm_storage_account" "tfstate_app" {
  name                = "pagopainfraterraform${var.env}"
  resource_group_name = "io-infra-rg"
}

data "azurerm_resource_group" "dashboards" {
  name = "dashboards"
}

data "azurerm_api_management_product" "product" {
  product_id          = local.domain
  api_management_name = "${local.product}-apim"
  resource_group_name = "${local.product}-api-rg"
}

data "azurerm_kubernetes_cluster" "aks" {
  name                = local.aks_name
  resource_group_name = local.aks_resource_group_name
}

data "azurerm_key_vault" "key_vault" {
  name                = "${local.product}-${local.domain}-kv"
  resource_group_name = "${local.product}-${local.domain}-sec-rg"
}
