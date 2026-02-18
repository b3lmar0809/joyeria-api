package com.joyeria.joyeria_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotEmpty(message = "La orden debe tener al menos un producto")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Email inválido")
    private String customerEmail;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String customerName;

    private String customerPhone;

    @NotBlank(message = "La dirección es requerida")
    private String shippingAddress;

    @NotBlank(message = "La ciudad es requerida")
    private String shippingCity;

    @NotBlank(message = "El estado es requerido")
    private String shippingState;

    @NotBlank(message = "El código postal es requerido")
    private String shippingPostalCode;

    @NotBlank(message = "El país es requerido")
    private String shippingCountry;

    private String notes;
}