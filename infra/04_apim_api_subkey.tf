locals {
  product_api_config_auth = {
    product_id   = "product-api-config-auth"
    display_name = "ApiConfig SubKey"
    description  = "Product for API Configuration SubKey for B2B"
  }
}

###############
##  SUBKEY   ##
###############

resource "azurerm_api_management_api_version_set" "api_config_auth_api" {

  name                = format("%s-api-config-auth-api", var.env_short)
  api_management_name = local.apim.name
  resource_group_name = local.apim.rg
  display_name        = local.product_api_config_auth.display_name
  versioning_scheme   = "Segment"
}

module "apim_api_config_auth_api" {
  source = "./.terraform/modules/__v4__/api_management_api"

  name                  = format("%s-api-config-auth-api", var.env_short)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.product_api_config_auth.product_id, data.azurerm_api_management_product.apim_aca_integration_product.product_id, data.azurerm_api_management_product.technical_support_api_product.product_id]
  subscription_required = true

  version_set_id = azurerm_api_management_api_version_set.api_config_auth_api.id
  api_version    = "v1"

  description  = "Api configuration for B2B"
  display_name = "ApiConfig SubKey"
  path         = "apiconfig/auth/api"
  protocols    = ["https"]

  service_url = null

  content_format = "openapi"
  content_value = templatefile("./api/apiconfig_api/v1/_openapi.json.tpl", {
    host    = local.apim_hostname
    service = local.product_api_config_auth.product_id
  })

  xml_content = templatefile("./api/apiconfig_api/subkey/v1/_base_policy.xml.tpl", {
    hostname    = var.hostname
    origin      = "*"
    addMockResp = var.env_short != "d" ? "true" : "false"
  })
}
