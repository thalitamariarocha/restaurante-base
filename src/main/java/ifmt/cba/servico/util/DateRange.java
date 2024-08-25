package ifmt.cba.servico.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRange {

    public static List<LocalDate> getDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);  // Avançar para o próximo dia
        }

        return dates;
    }
}

