package com.tuempresa.distribuidora;

public class CalculoDespacho {

    public static double calcularCosto(double montoCompra, double distanciaKm) {
        if (montoCompra >= 50000 && distanciaKm <= 20) {
            return 0; // Envío gratis
        } else if (montoCompra >= 25000 && montoCompra < 50000) {
            return distanciaKm * 150;
        } else {
            return distanciaKm * 300;
        }
    }
}

