prefix    = "pagopa"
env       = "uat"
env_short = "u"
domain    = "apiconfig"

location_short = "weu"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Uat"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-api-config"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix = "uat.platform"
external_domain      = "pagopa.it"
hostname             = "weuuat.apiconfig.internal.uat.platform.pagopa.it"