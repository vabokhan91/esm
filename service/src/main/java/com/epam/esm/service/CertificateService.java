package com.epam.esm.service;


import com.epam.esm.entity.Certificate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Viachaslau_Bokhan on 11/15/2017.
 */
@Service
public class CertificateService {

    private static AtomicInteger counter = new AtomicInteger();

    private static List<Certificate> certificates;

    static {
        certificates = populateProducts();

    }

    public Certificate findCertificateById(int id) {
        for (Certificate certificate : certificates) {
            if (id == certificate.getId()) {
                return certificate;
            }
        }
        return null;
    }

    private static List<Certificate> populateProducts() {
        List<Certificate> products = new ArrayList<Certificate>();
        products.add(new Certificate(counter.incrementAndGet(), "Mobile", 25498.00));
        products.add(new Certificate(counter.incrementAndGet(), "Desktop", 32658.00));
        products.add(new Certificate(counter.incrementAndGet(), "Laptop", 52147.00));
        products.add(new Certificate(counter.incrementAndGet(), "Tab", 18254.00));
        return products;
    }
}
