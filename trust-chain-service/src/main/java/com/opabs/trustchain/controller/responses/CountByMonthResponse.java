package com.opabs.trustchain.controller.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CountByMonthResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM-yy", timezone = "UTC")
    private Date month;

    private Long count;

}
