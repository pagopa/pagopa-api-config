locals {
  github = {
    org        = "pagopa"
    repository = "pagopa-api-config"
  }
  product = "${var.prefix}-${var.env_short}"

  location_short  = "weu"

  aks_name                = "${local.product}-${local.location_short}-${var.env}-aks"
  aks_resource_group_name = "${local.product}-${local.location_short}-${var.env}-aks-rg"
}
