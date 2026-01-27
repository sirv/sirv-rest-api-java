package com.sirv.api.model;

/**
 * Billing plan information.
 */
public class BillingPlan {
    private String name;
    private String planId;
    private Long storage;
    private Long dataTransferLimit;
    private Double price;
    private String currency;
    private String period;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Long getStorage() {
        return storage;
    }

    public void setStorage(Long storage) {
        this.storage = storage;
    }

    public Long getDataTransferLimit() {
        return dataTransferLimit;
    }

    public void setDataTransferLimit(Long dataTransferLimit) {
        this.dataTransferLimit = dataTransferLimit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "BillingPlan{" +
                "name='" + name + '\'' +
                ", storage=" + storage +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
