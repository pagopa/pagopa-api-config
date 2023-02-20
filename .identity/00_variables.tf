variable "env" {
  type = string
}

variable "env_short" {
  type = string
}

variable "prefix" {
  type    = string
  default = "pagopa"
  validation {
    condition = (
    length(var.prefix) <= 6
    )
    error_message = "Max length is 6 chars."
  }
}

variable "environment_cd_roles" {
  type = object({
    subscription = list(string)
  })
  description = "GitHub Continous Delivery roles"
}
