package com.meomulm.payment.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private int payment_id;
    private int reservation_id;
    private String payment_method;
    private int paid_amount;
    private String status;
    private String paid_at;
}
