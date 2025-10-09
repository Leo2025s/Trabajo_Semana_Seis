package com.tuempresa.distribuidora;

public class CalculoDespacho {
    public static double calcularCosto(double monto, double distancia) {
        if (monto >= 50000 && distancia <= 20) return 0;
        else if (monto >= 25000) return distancia * 150;
        else return distancia * 300;
    }
}


