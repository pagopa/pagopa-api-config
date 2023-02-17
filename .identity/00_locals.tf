locals {
  github = {
    org        = "pagopa"
    repository = "pagopa-api-config"
  }
  domain = "apiconfig"
  location_short  = "weu"

  product = "${var.prefix}-${var.env_short}"

  aks_name                = "${local.product}-${local.location_short}-${var.env}-aks"
  aks_resource_group_name = "${local.product}-${local.location_short}-${var.env}-aks-rg"
}
