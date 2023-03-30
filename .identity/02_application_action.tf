resource "azuread_application" "action" {
  display_name = "github-${local.github.org}-${local.github.repository}-${var.env}"
}

resource "azuread_service_principal" "action" {
  application_id = azuread_application.action.application_id
}

resource "azuread_application_federated_identity_credential" "environment" {
  application_object_id = azuread_application.action.object_id
  display_name          = "github-federated"
  description           = "github-federated"
  audiences             = ["api://AzureADTokenExchange"]
  issuer                = "https://token.actions.githubusercontent.com"
  subject               = "repo:${local.github.org}/${local.github.repository}:environment:${var.env}"
}

output "azure_action_client_id" {
  value = azuread_service_principal.action.application_id
}

output "azure_action_application_id" {
  value = azuread_service_principal.action.application_id
}

output "azure_action_object_id" {
  value = azuread_service_principal.action.object_id
}
