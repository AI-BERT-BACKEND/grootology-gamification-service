package com.aibert.dosw.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectProgressBatchRequestDTO {
  @NotEmpty @Valid private List<SubjectProgressDataDTO> subjects;
}
