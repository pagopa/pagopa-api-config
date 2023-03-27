variable "env" {
  type = string
}

variable "env_short" {
  type = string
}


variable "github_repository_environment" {
  type = object({
    protected_branches     = bool
    custom_branch_policies = bool
    reviewers_teams        = list(string)
  })
  description = "GitHub Continuous Integration roles"
  default     = {
    protected_branches     = false
    custom_branch_policies = true
    reviewers_teams        = ["pagopa-tech"]
  }

}


variable "k8s_kube_config_path_prefix" {
  type    = string
  default = "~/.kube"
}
