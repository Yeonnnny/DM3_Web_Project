package net.kdigital.ec21.dto.check;

public enum ProductCategory {
    FOOD_BEVERAGE("FB"),
    CHEMICAL("CH"),
    HEALTH_MEDICAL("HM"),
    ELECTRONIC("EL"),
    COSMETIC("CO");

    private String categoryCode;

    private ProductCategory(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

}
