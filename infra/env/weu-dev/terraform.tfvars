prefix    = "pagopa"
env       = "dev"
env_short = "d"
domain    = "apiconfig"

location_short = "weu"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Dev"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-api-config"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix = "dev.platform"
external_domain      = "pagopa.it"
hostname             = "weudev.apiconfig.internal.dev.platform.pagopa.it"