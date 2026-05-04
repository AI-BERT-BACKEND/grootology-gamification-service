package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.user.ActionEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ActionEventRequestDTO {
    @NotNull
    private ActionEvent event;
}
