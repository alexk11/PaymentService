package com.iprody.controller;

import com.iprody.model.PaymentDto;
import com.iprody.service.PaymentService;
import com.iprody.specification.PaymentFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log =
            LoggerFactory.getLogger(PaymentController.class);

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
        log.info("GET: search payments");
        final Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public ResponseEntity<List<PaymentDto>> fetchAll() {
        log.info("GET: get all payments");
        return ResponseEntity.ok().body(this.paymentService.getPayments());
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        log.info("POST: save one payment: \n\n {} \n", paymentDto.toString());
        //final PaymentDto savedPayment = this.paymentService.create(paymentDto);
        final PaymentDto savedPayment = this.paymentService.createAsync(paymentDto);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPayment.getGuid())
            .toUri();
        log.debug("Payment saved: {}", savedPayment);
        return ResponseEntity.created(location).body(savedPayment);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','READER')")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID id) {
        log.info("GET: get payment by id {}", id);
        final PaymentDto dto = this.paymentService.get(id);
        log.debug("Sending response PaymentDto: {}", dto);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping(path = "/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        log.info("PUT: update payment by id {} and dto {}", id, paymentDto);
        return ResponseEntity.ok().body(this.paymentService.update(id, paymentDto));
    }

    @PutMapping(path = "/update/{id}/{note}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updatePaymentNote(@PathVariable UUID id, @PathVariable String note) {
        log.info("PUT: update payment note by id {} and new note {}", id, note);
        return ResponseEntity.ok().body(this.paymentService.updateNote(id, note));
    }

    @DeleteMapping(path = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePayment(@PathVariable UUID id) {
        log.info("DELETE: delete payment by id {}", id);
        this.paymentService.delete(id);
    }

}
