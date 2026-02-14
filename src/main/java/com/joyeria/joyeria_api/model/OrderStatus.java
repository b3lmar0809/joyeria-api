package com.joyeria.joyeria_api.model;

// Enum que define todos los posibles estados de una orden
public enum OrderStatus {
    PENDING,     // creada pero no pagada
    PAID,        // pagada y confirmada
    PROCESSING,  // reparando el envio
    SHIPPED,     // enviada (en camino)
    DELIVERED,   // entregada al cliente
    CANCELLED,   // cancelada
    REFUNDED     // dinero devuelto
}

// flujo tipico:
// PENDING → PAID → PROCESSING → SHIPPED → DELIVERED