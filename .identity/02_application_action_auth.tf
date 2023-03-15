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

resource "azurerm_role_assignment" "environment_runner_github_runner_rg" {
  scope                = data.azurerm_resource_group.github_runner_rg.id
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


#resource "azuread_directory_role_assignment" "action_directory_readers" {
#  role_id             = azuread_directory_role.directory_readers.template_id
#  principal_object_id = azuread_service_principal.action.object_id
#}

resource "azurerm_role_assignment" "action_subscription" {
  for_each             = toset(var.action_roles.subscription)
  scope                = data.azurerm_subscription.current.id
  role_definition_name = each.key
  principal_id         = azuread_service_principal.action.object_id
}

resource "azurerm_role_assignment" "action_tfstate_inf" {
  scope                = data.azurerm_storage_account.tfstate_inf.id
  role_definition_name = "Storage Blob Data Contributor"
  principal_id         = azuread_service_principal.action.object_id
}





resource "kubernetes_cluster_role_binding" "action_view_extra_binding" {
  metadata {
    name = "action-view-extra-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "view-extra"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  depends_on = [
    data.azurerm_kubernetes_cluster.aks
  ]
}

resource "kubernetes_cluster_role_binding" "action_edit_extra_binding" {
  metadata {
    name = "action-edit-extra-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "view-extra"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  depends_on = [
    data.azurerm_kubernetes_cluster.aks
  ]
}

resource "kubernetes_cluster_role_binding" "action_edit_binding" {
  metadata {
    name = "action-edit-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "edit"
  }

  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  depends_on = [
    data.azurerm_kubernetes_cluster.aks
  ]
}

resource "kubernetes_cluster_role_binding" "action_view_binding" {
  metadata {
    name = "action-view-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "view"
  }
  subject {
    kind      = "Group"
    name      = azuread_service_principal.action.object_id
    namespace = "kube-system"
  }

  depends_on = [
    data.azurerm_kubernetes_cluster.aks
  ]
}
