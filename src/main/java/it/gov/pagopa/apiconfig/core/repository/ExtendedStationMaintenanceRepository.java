package it.gov.pagopa.apiconfig.core.repository;

import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import it.gov.pagopa.apiconfig.starter.repository.StationMaintenanceRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ExtendedStationMaintenanceRepository extends StationMaintenanceRepository {

    @Query(value =
            "SELECT m " +
                    "FROM StationMaintenance m JOIN Stazioni s ON m.fkStation = s.objId " +
                    "WHERE s.idStazione = :stationCode " +
                    "AND (" +
                    "(m.startDateTime <= :startDateTime AND :startDateTime < m.endDateTime) " +
                    "OR (m.startDateTime < :endDateTime AND :endDateTime <= m.endDateTime) " +
                    "OR (:startDateTime < m.startDateTime AND m.endDateTime < :endDateTime) " +
                    ")")
    List<StationMaintenance> findOverlappingMaintenance(
            @Param("stationCode") String stationCode,
            @Param("startDateTime") OffsetDateTime startDateTime,
            @Param("endDateTime") OffsetDateTime endDateTime
    );

    @Query(value =
            "SELECT m " +
                    "FROM StationMaintenance m " +
                    "WHERE (cast(cast(:startDateTimeBefore as text) as timestamp) IS NULL OR m.startDateTime < cast(cast(:startDateTimeBefore as text) as timestamp)) " +
                    "AND (cast(cast(:startDateTimeAfter as text) as timestamp) IS NULL OR m.startDateTime > cast(cast(:startDateTimeAfter as text) as timestamp)) " +
                    "AND (cast(cast(:endDateTimeBefore as text) as timestamp) IS NULL OR m.endDateTime < cast(cast(:endDateTimeBefore as text) as timestamp)) " +
                    "AND (cast(cast(:endDateTimeAfter as text) as timestamp) IS NULL OR m.endDateTime > cast(cast(:endDateTimeAfter as text) as timestamp))")
    List<StationMaintenance> findAllStationsMaintenances(
            @Param("startDateTimeBefore") OffsetDateTime startDateTimeBefore,
            @Param("startDateTimeAfter") OffsetDateTime startDateTimeAfter,
            @Param("endDateTimeBefore") OffsetDateTime endDateTimeBefore,
            @Param("endDateTimeAfter") OffsetDateTime endDateTimeAfter
    );
}
