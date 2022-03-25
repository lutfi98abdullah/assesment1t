package com.vttp.ssf.assesment.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.vttp.ssf.assesment.Model.Quotation;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class QuotationService {

    private final String url = "https://quotation.chuklee.com/quotation";
        
    public Optional<Quotation> getQuotations(List<String> items) {
        JsonArrayBuilder jArrayB = Json.createArrayBuilder();

        for(String item : items) {
            jArrayB.add(item);
        }

        RequestEntity<String> req = RequestEntity
            .post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(jArrayB.build().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        if(resp.getStatusCodeValue() >= 400)
            return Optional.empty();

        String quoteString = resp.getBody();
        

        InputStream is = new ByteArrayInputStream(quoteString.getBytes());
        JsonReader r = Json.createReader(is);
        JsonObject o = r.readObject();

        Quotation q = new Quotation();
        q.setQuoteId(o.getString("quoteId"));

        JsonArray quotationsArray = o.getJsonArray("quotations");
        for(int i = 0; i < quotationsArray.size(); i++) {
            JsonObject item = quotationsArray.getJsonObject(i);
            String itemName = item.getString("item");
            Double unitPrice = item.getJsonNumber("unitPrice").doubleValue();
            q.addQuotation(itemName, unitPrice.floatValue());
        }

        return Optional.of(q);
    }
}