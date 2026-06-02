package com.rutusoft.flowable.dto;
import java.util.List;
import com.rutusoft.flowable.dto.ItemDto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {

    private String processInstanceId;
    private String initiator;
    private String status;

    private String supplierCompanyName;
    private String supplierCompanyStreet;
    private String supplierCompanySuburb;
    private String supplierCompanyCity;
    private String supplierCompanyState;
    private String supplierCompanyPostcode;
    private String supplierCompanyCountry;

    private String vendorCompanyName;
    private String vendorName;
    private String vendorCompanyStreet;
    private String vendorCompanySuburb;
    private String vendorCompanyCity;
    private String vendorCompanyState;
    private String vendorCompanyPostcode;
    private String vendorCompanyCountry;
    private String vendorEmail;
    private String vendorCompanyAddressType;

    private String sims;
    private Double grandTotal;
    private List<ItemDto> items;
}
