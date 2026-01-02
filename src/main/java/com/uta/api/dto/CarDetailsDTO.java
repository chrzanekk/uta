package com.uta.api.dto;

import com.uta.api.enumeration.EuNorm;

public record CarDetailsDTO(String registrationNumber,
                            int productionYear,
                            EuNorm norm) {
}
