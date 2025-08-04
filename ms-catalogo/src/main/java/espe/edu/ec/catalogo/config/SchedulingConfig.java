package espe.edu.ec.catalogo.config;


import espe.edu.ec.catalogo.service.ClockProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    private ClockProducer clockProducer;

    @Scheduled(fixedRate = 10000)
    public void timeReport(){
        try{
            clockProducer.sendTime();
            System.out.println("hora enviaaaaaaaaada");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
