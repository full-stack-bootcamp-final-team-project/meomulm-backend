package com.meomulm.payment.model.mapper;


import com.meomulm.payment.model.dto.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payment payment);
    void deletePayment(int reservationId);
}
