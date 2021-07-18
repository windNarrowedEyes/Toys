package com.haibara.toys.shell;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private boolean success;
    private String output;
    private String errorOutput;
}
