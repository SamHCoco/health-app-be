package com.samhcoco.healthapp.core.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {
    private boolean success;
    private String message;
}
