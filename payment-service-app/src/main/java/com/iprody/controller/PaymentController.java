package com.iprody.controller;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.service.PaymentService;
import com.iprody.specification.PaymentFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/search")
    public Page<PaymentEntity> searchPayments(
        @ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "amount") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        final Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> fetchAll() {
        return ResponseEntity.ok().body(this.paymentService.fetchAllPayments());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PaymentDto> fetchPayment(@PathVariable UUID id) {
        return ResponseEntity.ok().body(this.paymentService.fetchSinglePayment(id));
    }

    @PostMapping(path = "/addPayment")
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(this.paymentService.processPayment(paymentDto));
    }

}
