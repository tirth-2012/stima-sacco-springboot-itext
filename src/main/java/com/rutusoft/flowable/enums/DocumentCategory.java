package com.rutusoft.flowable.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DocumentCategory {
    PROFORMA_INVOICE("Proforma Invoice"),
    PURCHASE_ORDER("Purchase Order"),
    INVOICE("Invoice");


    private final String category;

    public String getCategory() {
        return category;
    }
}
