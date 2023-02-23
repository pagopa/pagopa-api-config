package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WfespPluginConfRepository extends JpaRepository<WfespPluginConf, Long> {

  Optional<WfespPluginConf> findByIdServPlugin(String id);
}
