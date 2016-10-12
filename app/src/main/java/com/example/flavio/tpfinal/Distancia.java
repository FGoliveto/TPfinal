package com.example.flavio.tpfinal;


class Distancia {
    double distancia(Double lati1, Double long1, Double lati2, Double long2) {
        int radioDeLaTierra = 6371;
        double lat1 = lati1;
        double lat2 = lati2;
        double lon1 = long1;
        double lon2 = long2;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double resultado = radioDeLaTierra * c;
        return resultado/1;
    }
}
