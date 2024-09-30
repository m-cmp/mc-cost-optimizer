package com.mcmp.collector.model.cur;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class AwsCurModel {

    private String lineitem_usageaccountid;
    private String lineitem_productcode;
    private String lineitem_resourceid;
    private String lineitem_lineitemtype;
    private String product_instancetype;
    private String pricing_unit;
    private LocalDateTime lineitem_usagestartdate;
    private LocalDateTime lineitem_usageenddate;
    private String lineitem_usageamount;
    private String lineitem_unblendedcost;
    private String lineitem_blendedcost;
    private String pricing_publicondemandcost;
    private String pricing_publicondemandrate;
    private String lineitem_currencycode;
    private LocalDateTime collect_date;
    private String seq;
    private String product_sku;
    private String product_region;
    private String product_instanceFamily;
    private String product_location;
    private String lineitem_operation;
    private String product_instancetypefamily;
    private String lineitem_usagetype;
    private String product_vcpu;
    private String product_memory;

    public AwsCurModel(String lineitem_usageaccountid,
                       String lineitem_productcode,
                       String lineitem_resourceid,
                       String lineitem_lineitemtype,
                       String product_instancetype,
                       String pricing_unit,
                       String lineitem_usageamount,
                       String lineitem_unblendedcost,
                       String lineitem_blendedcost,
                       LocalDateTime lineitem_usagestartdate,
                       LocalDateTime lineitem_usageenddate,
                       String pricing_publicondemandcost,
                       String pricing_publicondemandrate,
                       String lineitem_currencycode,
                       LocalDateTime collect_date,
                       String seq,
                       String product_sku,
                       String product_region,
                       String product_instanceFamily,
                       String product_location,
                       String lineitem_operation,
                       String product_instancetypefamily,
                       String lineitem_usagetype,
                       String product_vcpu,
                       String product_memory){
        this.lineitem_usageaccountid = lineitem_usageaccountid;
        this.lineitem_productcode = lineitem_productcode;
        this.lineitem_resourceid = lineitem_resourceid;
        this.lineitem_lineitemtype = lineitem_lineitemtype;
        this.product_instancetype = product_instancetype;
        this.pricing_unit = pricing_unit;
        this.lineitem_usageamount = lineitem_usageamount;
        this.lineitem_unblendedcost = lineitem_unblendedcost;
        this.lineitem_blendedcost = lineitem_blendedcost;
        this.lineitem_usagestartdate = lineitem_usagestartdate;
        this.lineitem_usageenddate = lineitem_usageenddate;
        this.pricing_publicondemandcost = pricing_publicondemandcost;
        this.pricing_publicondemandrate = pricing_publicondemandrate;
        this.lineitem_currencycode = lineitem_currencycode;
        this.collect_date = collect_date;
        this.seq = seq;
        this.product_sku = product_sku;
        this.product_region = product_region;
        this.product_instanceFamily = product_instanceFamily;
        this.product_location = product_location;
        this.lineitem_operation = lineitem_operation;
        this.product_instancetypefamily = product_instancetypefamily;
        this.lineitem_usagetype = lineitem_usagetype;
        this.product_vcpu = product_vcpu;
        this.product_memory = product_memory;
    }

}
