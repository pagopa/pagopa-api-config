data "azurerm_storage_account" "tfstate_app" {
  name                = "tfapp${lower(replace(data.azurerm_subscription.current.display_name, "-", ""))}"
  resource_group_name = "terraform-state-rg"
}

data "azurerm_storage_account" "tfstate_inf" {
  name                = "tfinf${lower(replace(data.azurerm_subscription.current.display_name, "-", ""))}"
  resource_group_name = "terraform-state-rg"
}

data "azurerm_resource_group" "dashboards" {
  name = "dashboards"
}

data "azurerm_api_management_product" "product" {
  product_id          = "${local.domain}-core-subkey" # TODO other product oauth
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

data "azurerm_resource_group" "github_runner_rg" {
  name = "${local.project}-github-runner-rg"
}

data "github_organization_teams" "all" {
  root_teams_only = true
  summary_only    = true
}

resource "azuread_directory_role" "directory_readers" {
  display_name = "Directory Readers"
}

data "azuread_group" "developers_group"{
  display_name = "pagopa-d-adgroup-developers"
}
