package org.dolniak.jtop;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends CrudRepository<ActionLogEntity, Long> {}