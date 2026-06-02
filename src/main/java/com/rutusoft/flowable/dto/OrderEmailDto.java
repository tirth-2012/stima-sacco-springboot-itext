package com.rutusoft.flowable.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderEmailDto {

    // Initiator
    private String initiator;

    // Supplier details
    private String supplierCompanyName;
    private String supplierCompanyStreet;
    private String supplierCompanySuburb;
    private String supplierCompanyCity;
    private String supplierCompanyState;
    private String supplierCompanyPostcode;
    private String supplierCompanyCountry;

    // Vendor details
    private String vendorCompanyName;
    private String vendorName;
    private String vendorCompanyStreet;
    private String vendorCompanySuburb;
    private String vendorCompanyCity;
    private String vendorCompanyState;
    private String vendorCompanyPostcode;
    private String vendorCompanyCountry;

    // Order details
    private List<ItemDto> items;
    private Double grandTotal;

    // Email metadata
    private String toEmail;
}