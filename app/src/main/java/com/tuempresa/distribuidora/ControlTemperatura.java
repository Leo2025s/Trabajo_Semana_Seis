package com.tuempresa.distribuidora;

public class ControlTemperatura {

    private static final double LIMITE_TEMPERATURA = -18.0; // °C congelados

    public static boolean verificarTemperatura(double temperaturaActual) {
        if (temperaturaActual > LIMITE_TEMPERATURA) {
            System.out.println("⚠ Alerta: Cadena de frío comprometida. Temperatura: " + temperaturaActual + "°C");
            return false;
        }
        return true;
    }
}

