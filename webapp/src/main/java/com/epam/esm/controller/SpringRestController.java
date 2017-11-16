package com.epam.esm.controller;



import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.User;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringRestController {

        @Autowired
        private CertificateService productService;

        @Autowired
        private UserService userService;

        @RequestMapping(value = "/certificate/{id}", method = RequestMethod.GET)
        public ResponseEntity<Certificate> getCertificateByid(@PathVariable("id") int id) {
            Certificate product = productService.findCertificateById(id);
            if (product == null) {
                return new ResponseEntity<Certificate>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Certificate>(product, HttpStatus.OK);
        }

        /*@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
        public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
            if (user == null) {
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
        return new ResponseEntity<User>(user, HttpStatus.OK);
        }*/



}
