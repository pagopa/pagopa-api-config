locals {
  github = {
    org        = "pagopa"
    repository = "pagopa-api-config"
  }
  domain = "apiconfig"
  location_short  = "weu"

  product = "${var.prefix}-${var.env_short}"
  project = "${var.prefix}-${var.env_short}-${local.location_short}-${local.domain}"

  aks_name                = "${local.product}-${local.location_short}-${var.env}-aks"
  aks_resource_group_name = "${local.product}-${local.location_short}-${var.env}-aks-rg"

  pagopa_apim_name = "${local.product}-apim"
  pagopa_apim_rg   = "${local.product}-api-rg"
}
