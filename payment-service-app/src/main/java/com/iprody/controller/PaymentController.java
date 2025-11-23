package com.iprody.controller;

import com.iprody.model.PaymentDto;
import com.iprody.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> fetchAll() {
        return ResponseEntity.ok().body(this.paymentService.fetchAllPayments());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PaymentDto> fetchPayment(@PathVariable String id) {
        return ResponseEntity.ok().body(this.paymentService.fetchSinglePayment(Long.parseLong(id)));
    }

    @PostMapping(path = "/addPayment")
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(this.paymentService.processPayment(paymentDto));
    }

}
