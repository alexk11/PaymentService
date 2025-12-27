package com.iprody.controller;

import com.iprody.model.PaymentDto;
import com.iprody.service.PaymentService;
import com.iprody.specification.PaymentFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PaymentDto> searchPayments(
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
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public ResponseEntity<List<PaymentDto>> fetchAll() {
        return ResponseEntity.ok().body(this.paymentService.getPayments());
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        final PaymentDto savedPayment = this.paymentService.create(paymentDto);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPayment.getGuid())
            .toUri();
        return ResponseEntity.created(location).body(savedPayment);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','READER')")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok().body(this.paymentService.get(id));
    }

    @PutMapping(path = "/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(this.paymentService.update(id, paymentDto));
    }

    @PutMapping(path = "/update/{id}/{note}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updatePaymentNote(@PathVariable UUID id, @PathVariable String note) {
        return ResponseEntity.ok().body(this.paymentService.updateNote(id, note));
    }

    @DeleteMapping(path = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePayment(@PathVariable UUID id) {
        this.paymentService.delete(id);
    }

}
