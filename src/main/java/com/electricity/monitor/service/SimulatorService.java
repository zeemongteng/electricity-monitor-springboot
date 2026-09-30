package com.electricity.monitor.service;

import com.electricity.monitor.dto.ReadingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Random;

@Service
public class SimulatorService {
    private final ElectricityService service;
    private final Random random=new Random();

    @Value("${app.simulator.enabled:true}") private boolean enabled;
    @Value("${app.simulator.meter-id:SIM-001}") private String meterId;

    private final ZoneId zone=ZoneId.of("Asia/Bangkok");

    public SimulatorService(ElectricityService service){this.service=service;}

    @Scheduled(fixedRateString="${app.simulator.interval-ms:10000}")
    public void generateReading(){
        if(!enabled) return;

        double base=0.05+random.nextDouble()*0.08;
        boolean spike=random.nextDouble()<0.10;
        double kwh=spike ? base*(2.0+random.nextDouble()) : base;

        double voltage=220+random.nextGaussian()*3;
        double power=kwh*1000.0;
        double current=Math.max(0.1,power/voltage);

        service.saveReading(new ReadingRequest(
            meterId,OffsetDateTime.now(zone),voltage,current,power,kwh));
    }
}
