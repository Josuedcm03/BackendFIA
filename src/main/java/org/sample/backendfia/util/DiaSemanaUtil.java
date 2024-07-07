package org.sample.backendfia.util;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class DiaSemanaUtil {
    private static final Map<DayOfWeek, String> diaSemanaMap = new HashMap<>();

    static {
        diaSemanaMap.put(DayOfWeek.MONDAY, "Lunes");
        diaSemanaMap.put(DayOfWeek.TUESDAY, "Martes");
        diaSemanaMap.put(DayOfWeek.WEDNESDAY, "Miércoles");
        diaSemanaMap.put(DayOfWeek.THURSDAY, "Jueves");
        diaSemanaMap.put(DayOfWeek.FRIDAY, "Viernes");
        diaSemanaMap.put(DayOfWeek.SATURDAY, "Sábado");
        diaSemanaMap.put(DayOfWeek.SUNDAY, "Domingo");
    }

    public static String getDiaSemanaEnEspanol(DayOfWeek dia) {
        return diaSemanaMap.get(dia);
    }
}
