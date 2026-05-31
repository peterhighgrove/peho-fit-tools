package se.peho.fittools.core.strings;

import com.garmin.fit.GarminProduct;
import com.garmin.fit.Manufacturer;

public class ProductStr {

    private String nameStr = null;

    private static String safeManufacturer(Integer manufNo) {
        if (manufNo == null) {
            return "";
        }
        String manufacturer = Manufacturer.getStringFromValue(manufNo);
        return manufacturer != null ? manufacturer : "";
    }

    private static String safeProduct(Integer productNo) {
        if (productNo == null) {
            return "";
        }
        String product = GarminProduct.getStringFromValue(productNo);
        return product != null ? product : "";
    }

    public ProductStr(Integer manufNo, Integer productNo, Float swVersionNo) {
        // DeviceInfo
        // --------------------------------
        String manuf = safeManufacturer(manufNo);
        String product = safeProduct(productNo);

        if (StringsDebug.enabled) System.out.println("  Extracted From DevInfo Manufacturer: " + manuf + "(" + manufNo + ")"
            + ", Product: " + product + "(" + productNo + ")"
            + ", sw:" + swVersionNo
            );

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

        String formattedProductString = (manuf != null && !manuf.isEmpty() ? manuf : "")
            + (product != null && !product.isEmpty() ? ("-" + product) : "")
            + (swVersion != null && !swVersion.isEmpty() ? ("-" + swVersion) : "")
            ;

        if (StringsDebug.enabled) {
            System.out.print("    => Before combining: Manufacturer: " + manuf + "(" + manufNo + ")"
                + ", Product: " + product + "(" + productNo + ")"
                + ", sw:" + swVersion
                );
            System.out.println(" => Formatted:'" + formattedProductString + "'");
        }

        nameStr = formattedProductString;
    }

    public ProductStr(Integer manufNo, Integer productNo, Integer manufNo2, Integer productNo2, Float swVersionNo) {

        // DeviceInfo
        // --------------------------------
        String manuf = Manufacturer.getStringFromValue(manufNo);
        String product = GarminProduct.getStringFromValue(productNo);

        if (StringsDebug.enabled) System.out.println("  Extracted From DevInfo Manufacturer: " + manuf + "(" + manufNo + ")"
            + ", Product: " + product + "(" + productNo + ")"
            + ", sw:" + swVersionNo
            );

        // FileIdInfo
        // --------------------------------
        String manuf2 = safeManufacturer(manufNo2);
        String product2 = safeProduct(productNo2);

        if (StringsDebug.enabled) System.out.println("                  From FileId Manufacturer: " + manuf2 + "(" + manufNo2 + ")"
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

        String formattedProductString = (manuf != null && !manuf.isEmpty() ? manuf : "")
            + (product != null && !product.isEmpty() ? ("-" + product) : "")
            + (swVersion != null && !swVersion.isEmpty() ? ("-" + swVersion) : "")
            ;

        if (StringsDebug.enabled) {
            System.out.print("    => Before combining: Manufacturer: " + manuf + "(" + manufNo + ")"
                + ", Product: " + product + "(" + productNo + ")"
                + ", sw:" + swVersion
                );
            System.out.println(" => Formatted:'" + formattedProductString + "'");
        }

        nameStr = formattedProductString;
    }

    public String get() {
        return nameStr;
    }

    public static String get(Integer manufNo, Integer productNo, Float swVersionNo) {
        return new ProductStr(manufNo, productNo, swVersionNo).get();
    }

    public static String get(Integer manufNo, Integer productNo, Integer manufNo2, Integer productNo2, Float swVersionNo) {
        return new ProductStr(manufNo, productNo, manufNo2, productNo2, swVersionNo).get();
    }
}
