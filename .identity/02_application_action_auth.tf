resource "azurerm_role_assignment" "environment_terraform_subscription" {
  scope                = data.azurerm_subscription.current.id
  role_definition_name = "Reader"
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "environment_terraform_storage_account_tfstate_app" {
  scope                = data.azurerm_storage_account.tfstate_app.id
  role_definition_name = "Contributor"
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "environment_terraform_resource_group_dashboards" {
  scope                = data.azurerm_resource_group.dashboards.id
  role_definition_name = "Contributor"
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "product" {
  scope                = data.azurerm_api_management_product.product.id
  role_definition_name = "Contributor"
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "aks" {
  scope                = data.azurerm_kubernetes_cluster.aks.id
  role_definition_name = "Contributor"
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "key_vault" {
  scope                = data.azurerm_key_vault.key_vault.id
  role_definition_name = "Reader"
  principal_id         = azuread_service_principal.action.object_id
}
