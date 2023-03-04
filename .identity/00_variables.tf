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

variable "action_roles" {
  type = object({
    subscription = list(string)
  })
  description = "GitHub Continuous Delivery roles"
}

variable "github_repository_environment" {
  type = object({
    protected_branches     = bool
    custom_branch_policies = bool
    reviewers_teams        = list(string)
  })
  description = "GitHub Continuous Integration roles"
}


variable "k8s_kube_config_path_prefix" {
  type    = string
  default = "~/.kube"
}
