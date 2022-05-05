package io.cloudflight.jems.server.project.service.checklist.model.metadata

import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ChecklistInstanceMetadata
import java.math.BigDecimal

class ScoreInstanceMetadata(val score: BigDecimal, val justification: String) : ChecklistInstanceMetadata
