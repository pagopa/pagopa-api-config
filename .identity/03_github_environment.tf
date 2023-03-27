resource "github_repository_environment" "github_repository_environment" {
  environment = var.env
  repository  = local.github.repository
  # filter teams reviewers from github_organization_teams
  # if reviewers_teams is null no reviewers will be configured for environment
  #  dynamic "reviewers" {
  #    for_each = (var.github_repository_environment.reviewers_teams == null ? [] : [1])
  #    content {
  #      teams = matchkeys(
  #        data.github_organization_teams.all.teams.*.id,
  #        data.github_organization_teams.all.teams.*.name,
  #        var.github_repository_environment.reviewers_teams
  #      )
  #    }
  #  }
  deployment_branch_policy {
    protected_branches     = var.github_repository_environment.protected_branches
    custom_branch_policies = var.github_repository_environment.custom_branch_policies
  }
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "azure_tenant_id" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "TENANT_ID"
  plaintext_value = data.azurerm_client_config.current.tenant_id
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "azure_subscription_id" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "SUBSCRIPTION_ID"
  plaintext_value = data.azurerm_subscription.current.subscription_id
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "azure_client_id" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "CLIENT_ID"
  plaintext_value = azuread_service_principal.action.application_id
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "azure_container_app_environment_name" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "CONTAINER_APP_ENVIRONMENT_NAME"
  plaintext_value = "${local.runner}-github-runner-cae"
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "azure_resource_group_name" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "RUNNER_RESOURCE_GROUP_NAME"
  plaintext_value = "${local.runner}-github-runner-rg"
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "cluster_resource_group_name" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "CLUSTER_RESOURCE_GROUP_NAME"
  plaintext_value = local.aks_resource_group_name
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_environment_secret" "cluster_name" {
  repository      = local.github.repository
  environment     = var.env
  secret_name     = "CLUSTER_NAME"
  plaintext_value = local.aks_name
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_secret" "secret_sonar_token" {
  count  = var.env_short == "d" ? 1 : 0

  repository       = local.github.repository
  secret_name      = "SONAR_TOKEN"
  plaintext_value  = data.azurerm_key_vault_secret.key_vault_sonar[0].value
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_secret" "secret_sonar_bot_token" {
  count  = var.env_short == "d" ? 1 : 0

  repository       = local.github.repository
  secret_name      = "BOT_TOKEN_GITHUB"
  plaintext_value  = data.azurerm_key_vault_secret.key_vault_bot_token[0].value
}

#tfsec:ignore:github-actions-no-plain-text-action-secrets # not real secret
resource "github_actions_secret" "secret_cucumber_token" {
  count  = var.env_short == "d" ? 1 : 0

  repository       = local.github.repository
  secret_name      = "CUCUMBER_PUBLISH_TOKEN"
  plaintext_value  = data.azurerm_key_vault_secret.key_vault_cucumber_token[0].value
}
