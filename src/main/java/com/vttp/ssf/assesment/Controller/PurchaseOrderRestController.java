package com.vttp.ssf.assesment.Controller;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vttp.ssf.assesment.Model.Quotation;
import com.vttp.ssf.assesment.Service.QuotationService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@RestController
@RequestMapping(path="/api", consumes = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService quotationSvc;
    
    @PostMapping(path="/po", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postPurchaseOrder(@RequestBody String payload) {

        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader r = Json.createReader(is);
        JsonObject o = r.readObject();

        String name = o.getString("name");
        JsonArray items = o.getJsonArray("Items");
        Map<String, Integer> itemMap = new HashMap<>();

    
        
        for(int i = 0; i < items.size(); i++) {
            JsonObject item = items.getJsonObject(i);
            itemMap.put(item.getString("item"), item.getInt("quantity"));
        }


        Optional<Quotation> opt = quotationSvc.getQuotations(new ArrayList<>(itemMap.keySet()));
         // return empty object
        if(opt.isEmpty()) {
            return ResponseEntity.badRequest().body("{}");
        }

        Quotation quotation = opt.get();
        double total = 0.0;
        for(Entry<String, Integer> entry : itemMap.entrySet()) {
            Float unitPrice = quotation.getQuotation(entry.getKey());
            total += entry.getValue() * unitPrice;
        }

        JsonObject obj = Json.createObjectBuilder()
                        .add("invoiceId", quotation.getQuoteId())
                        .add("name", name)
                        .add("total", total)
                        .build();

        return ResponseEntity.ok().body(obj.toString());
    }
}