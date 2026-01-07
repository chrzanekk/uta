package com.uta.api.enumeration;

import com.uta.api.dto.CarDetailsDTO;

import java.util.Arrays;
import java.util.List;

/***
 * Simple list of cars only for temporarily use, in normal application we store this in DB
 */

public enum CarsDetails {
    DACIA_DOKKER_1("LPU79226", 2014, EuNorm.EURO5),
    DACIA_DOKKER_2("LPU79227", 2014, EuNorm.EURO5),
    DACIA_DOKKER_3("LPU94733", 2014, EuNorm.EURO5),
    FIAT_DUCATO_1("LPU50205", 2015, EuNorm.EURO5),
    FIAT_DUCATO_2("LPU0940A", 2017, EuNorm.EURO6),
    FIAT_DUCATO_3("LU420KN", 2019, EuNorm.EURO6),
    FSO_LUBLIN("LPU25334", 2001, EuNorm.EURO2),
    RENAULT_MASTER_1("LPU39785", 2009, EuNorm.EURO4),
    RENAULT_MASTER_2("PO9VC81", 2021, EuNorm.EURO6),
    RENAULT_MEGANE("LPU45452", 2012, EuNorm.EURO5),
    RENAULT_TRAFFIC_III("LPU59777", 2014, EuNorm.EURO5),
    RENAULT_TRAFFIC_IV("LPU27738", 2011, EuNorm.EURO4),
    RENAULT_TRAFFIC_V("LU056NF", 2011, EuNorm.EURO4),
    FORD_TRANSIT_CONNECT("WZ758ET", 2017, EuNorm.EURO6),
    FORD_TURNEO("LPU79280", 2017, EuNorm.EURO6),
    FORD_TRANSIT("WY736VJ", 2021, EuNorm.EURO6),
    FORD_TRANSIT_CONNECT_1("WZ524EV", 2022, EuNorm.EURO6),
    FORD_TRANSIT_CONNECT_2("WZ523EV", 2022, EuNorm.EURO6),
    DACIA_JOGGER("LU327NF", 2022, EuNorm.EURO6),
    DISCOVERY("LPU84276", 2015, EuNorm.EURO5),
    VOLVO_XC90("LU019SY", 2024, EuNorm.EURO6),
    WAG1("WAG1", 2024, EuNorm.EURO2),
    WAG2("WAG2", 2024, EuNorm.EURO5),
    WAG3("WAG3", 2024, EuNorm.EURO5),
    WAG4("WAG4", 2024, EuNorm.EURO5),
    WAG5("WAG5", 2024, EuNorm.EURO4),
    WAG6("WAG6", 2024, EuNorm.EURO5),
    WAG7("WAG7", 2024, EuNorm.EURO2),
    WAG8("WAG8", 2024, EuNorm.EURO5);


    private final String registrationNumber;
    private final int productionYear;
    private final EuNorm norm;

    CarsDetails(String registrationNumber, int productionYear, EuNorm norm) {
        this.registrationNumber = registrationNumber;
        this.productionYear = productionYear;
        this.norm = norm;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public EuNorm getNorm() {
        return norm;
    }

    public CarDetailsDTO toDto() {
        return new CarDetailsDTO(this.registrationNumber, this.productionYear, this.norm);
    }

    public static List<CarDetailsDTO> getAllCars() {
        return Arrays.stream(values())
                .map(CarsDetails::toDto)
                .toList();
    }
}
