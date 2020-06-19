package energyservice;

import energyservice.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyHandler{

    @Autowired
    private AssignRepository assignRepository;

    @Transactional
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_Assign(@Payload Reserved reserved){

        if(reserved.isMe()){
            System.out.println("##### listener Assign : " + reserved.toJson());

            Assign newAssign = new Assign();
            newAssign.setName(reserved.getName());
            newAssign.setAddress(reserved.getAddress());
            newAssign.setReservationid(reserved.getId());
            newAssign.setMobileno(reserved.getMobileno());
            newAssign.setCenterid(reserved.getCenterid());

            if(reserved.getMdate() != null) {
                newAssign.setMdate(reserved.getMdate());
                newAssign.setMtime(reserved.getMtime());
            } else {
                newAssign.setMdate(reserved.getSdate());
                newAssign.setMtime(reserved.getStime());
            }

            newAssign.setStatus("R");
            assignRepository.save(newAssign);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelled_Assign(@Payload Cancelled cancelled){

        if(cancelled.isMe()){
            System.out.println("##### listener Assign : " + cancelled.toJson());

            Assign newAssign = new Assign();
            newAssign.setName(cancelled.getName());
            newAssign.setAddress(cancelled.getAddress());
            newAssign.setReservationid(cancelled.getId());
            newAssign.setMobileno(cancelled.getMobileno());
            newAssign.setCenterid(cancelled.getCenterid());
            assignRepository.delete(newAssign);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverChanged_Assign(@Payload Changed changed){

        if(changed.isMe()){
            System.out.println("##### listener Assign : " + changed.toJson());

            Assign newAssign = assignRepository.findByReservationid(changed.getId());
            if(newAssign != null){
                newAssign.setReservationid(changed.getId());
                newAssign.setMdate(changed.getMdate());
                newAssign.setMtime(changed.getMtime());

                assignRepository.save(newAssign);
            }
        }
    }

}
