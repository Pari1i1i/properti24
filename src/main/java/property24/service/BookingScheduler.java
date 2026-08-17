package property24.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingService bookingService;

    @Scheduled(fixedRate = 300000)
    public void checkOverdueBookings() {
        int expiredCount = bookingService.expireOverdueBookings();
        if (expiredCount > 0) {
            log.info("BookingScheduler: {} booking yang lewat batas waktu telah diubah menjadi kedaluwarsa.", expiredCount);
        }
    }
}
