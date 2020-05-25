package com.phones.phones.dto;

import com.phones.phones.model.LineStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineStatusDto implements Serializable {

    private LineStatus status;

}
