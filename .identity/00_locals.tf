locals {
  github = {
    org        = "pagopa"
    repository = "pagopa-api-config"
  }
  product = "${var.prefix}-${var.env_short}"
}
