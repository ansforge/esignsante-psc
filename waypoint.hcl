project = "cybersante/esignsante-psc"

labels = { "domaine" = "esignsante-psc" }

runner {
    enabled = true
    data_source "git" {
        ref = "var.datacenter"
    }
    poll {
        enabled = true
		interval = "24h"
    }
}

# An application to deploy.
app "cybersante/esignsante-psc" {
    build {
        use "docker" {
           dockerfile = "${path.app}/${var.dockerfile_path}"
        }
        registry {
           use "docker" {
             image = "${var.registry_path}/esignsante-psc"
             tag   = gitrefpretty()
             encoded_auth = filebase64("/secrets/dockerAuth.json")
           }
        }
    }

	deploy {
		use "nomad-jobspec" {
			jobspec = templatefile("${path.app}/esignsante-psc.nomad.tpl", {
				datacenter = var.datacenter
				proxy_host = var.proxy_host
				proxy_port = var.proxy_port
				non_proxy_host_list = var.non_proxy_host_list
				user_java_opts = var.user_java_opts
				swagger_ui = var.swagger_ui
				spring_http_multipart_max_file_size = var.spring_http_multipart_max_file_size
				spring_http_multipart_max_request_size = var.spring_http_multipart_max_request_size
				esignsantews_service_isexternal = var.esignsantews_service_isexternal
				esignsantews_host = var.esignsantews_host
				esignsantews_port = var.esignsantews_port
				psc_bypass = var.psc_bypass
				esignsantepsc_appserver_mem_size = var.esignsantepsc_appserver_mem_size
				logstash_host = var.logstash_host
			})
		}
	}
}

variable datacenter {
    type = string
    default = "test"
}

variable dockerfile_path {
    type = string
    default = "Dockerfile"
}

variable "registry_path" {
    type = string
    default = "registry.repo.proxy-dev-forge.asip.hst.fluxus.net/esignsante-psc"
}

variable "proxy_host" {
  type = string
  default = ""
}

variable "proxy_port" {
  type = string
  default = ""
}

variable "user_java_opts" {
  type = string
  default = ""
}

variable "swagger_ui" {
  type = string
  default = ""
}

variable "non_proxy_host_list" {
  type = string
  default = ""
}

variable "spring_http_multipart_max_file_size" {
  type = string
  default = "200MB"
}

variable "spring_http_multipart_max_request_size" {
  type = string
  default = "200MB"
}

variable "esignsantews_service_isexternal" {
  type = string
  default = ""
}

variable "esignsantews_host" {
  type = string
  default = "{{.Address}}"
}

variable "esignsantews_port" {
  type = string
  default = "{{.Port}}"
}

variable "psc_bypass" {
  type = bool
  default = false
}

variable "esignsantepsc_appserver_mem_size" {
  type = string
  default = "2048"
}

variable "logstash_host" {
  type = string
  default = ""
}
