package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.validation.constraints.*;

/**
 * FtpServer
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FtpServer {

    @JsonProperty("host")
    @Schema(example = "host.domain", required = true)
    @NotBlank
    private String host;

    @JsonProperty("port")
    @Schema(example = "1234", required = true)
    @NotNull
    @Positive
    private Integer port;

    @JsonProperty("username")
    @Schema(example = "username", required = true)
    @NotNull
    private String username;

    @JsonProperty("password")
    @Schema(example = "pwdpwdpwd", required = true)
    @NotNull
    private String password;

    @JsonProperty("root_path")
    @Schema(example = "/", required = true)
    @NotNull
    private String rootPath;

    @JsonProperty("service")
    @Schema(example = "service", required = true)
    @NotBlank
    private String service;

    @JsonProperty("type")
    @Schema(example = "out", required = true)
    @NotNull
    private String type;

    @JsonProperty("in_path")
    @Schema(example = "/in/service")
    private String inPath;

    @JsonProperty("out_path")
    @Schema(example = "/out/service")
    private String outPath;

    @JsonProperty("history_path")
    @Schema(example = "/out/history/service")
    private String historyPath;

    @JsonProperty("enabled")
    @Schema(defaultValue = "true")
    @NotNull
    private Boolean enabled;

}
