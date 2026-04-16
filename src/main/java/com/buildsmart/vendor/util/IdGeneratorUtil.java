package com.buildsmart.vendor.util;

import java.time.LocalDate;

public final class IdGeneratorUtil {

    private IdGeneratorUtil() {
        // prevent instantiation
    }

    public static String nextVendorId(String lastVendorId) {
        int next = extractNumericSuffix(lastVendorId, 3) + 1;
        return String.format("VENVN%03d", next);
    }

    public static String nextContractId(String lastContractId) {
        int next = extractNumericSuffix(lastContractId, 3) + 1;
        return String.format("CONBS%03d", next);
    }

    public static String nextDeliveryId(String lastDeliveryId) {
        int next = extractNumericSuffix(lastDeliveryId, 3) + 1;
        return String.format("DELBS%03d", next);
    }

    public static String nextInvoiceId(String lastInvoiceId) {
        int next = extractNumericSuffix(lastInvoiceId, 3) + 1;
        return String.format("INVBS%03d", next);
    }



    public static String nextProjectId(String lastProjectId) {
        int year = LocalDate.now().getYear() % 100;
        int next = extractNumericSuffix(lastProjectId, 3) + 1;
        return String.format("CHEBS%02d%03d", year, next);
    }

    /**
     * Extracts the numeric suffix of an ID.
     * Example: "VENVN005" -> 5
     */
    private static int extractNumericSuffix(String id, int length) {
        if (id == null || id.length() < length) {
            return 0;
        }

        try {
            String numericPart = id.substring(id.length() - length);
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}