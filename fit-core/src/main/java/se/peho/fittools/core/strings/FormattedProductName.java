package se.peho.fittools.core.strings;

import com.garmin.fit.GarminProduct;
import com.garmin.fit.Manufacturer;

public class FormattedProductName {

    private String name = null;

    public FormattedProductName(Integer manufNo, Integer productNo, Integer manufNo2, Integer productNo2, Float swVersionNo) {

        // DeviceInfo
        // --------------------------------
        String manuf = Manufacturer.getStringFromValue(manufNo);
        String product = GarminProduct.getStringFromValue(productNo);

        System.out.println("  Extracted info: From DevInfo Manufacturer: " + manuf + "(" + manufNo + ")"
            + ", Product: " + product + "(" + productNo + ")"
            + ", sw:" + swVersionNo
            );

        // FileIdInfo
        // --------------------------------
        String manuf2 = Manufacturer.getStringFromValue(manufNo2);
        String product2 = GarminProduct.getStringFromValue(productNo2);

        System.out.println("                  From FileId Manufacturer: " + manuf2 + "(" + manufNo2 + ")"
            + " Product: " + product2 + "(" + productNo2 + ")"
            );

        if (manuf.isEmpty()) {
            manuf = manuf2;
        }
        if (product.isEmpty()) {
            product = product2;
        }
        manuf = manuf
            .replace("CONCEPT2", "c2")
            .toLowerCase()
            ;

        product = product
            .replace("EPIX_GEN2_PRO_51", "e2pro")
            .replace("EPIX_GEN2", "e2")
            .replace("FENIX6X", "f6x")
            .toLowerCase()
            ;

        String swVersion = swVersionNo != null ? 
            String.format("v%.2f", swVersionNo)
            .replace(",", ".")
                : null;

        if (manuf.equals("c2")) {
            swVersion = null;
        }

        System.out.println("  Before combining: Manufacturer: " + manuf + "(" + manufNo + ")"
            + ", Product: " + product + "(" + productNo + ")"
            + ", sw:" + swVersion
            );

        this.name = (manuf != null && !manuf.isEmpty() ? manuf : "")
            + (product != null && !product.isEmpty() ? ("-" + product) : "")
            + (swVersion != null && !swVersion.isEmpty() ? ("-" + swVersion) : "")
            ;
    }

    public String getName() {
        return name;
    }
}
