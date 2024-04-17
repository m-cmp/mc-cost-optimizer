package com.mcmp.dummybe.model.common;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class VendorListModel {
    public static final List<String> vendors = Arrays.asList("GCP", "AWS", "AZURE", "OCI", "NCP");
}
