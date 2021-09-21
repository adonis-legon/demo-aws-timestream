package com.example.demoawstimestream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationIdentifier {
    private String countryCode;
    private String customerCode;
    private String subsidiaryCode;
    private String DeviceCode;
}
