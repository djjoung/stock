package chargingstation;

import chargingstation.config.kafka.KafkaProcessor;

import org.springframework.beans.BeanUtils;
// import com.fasterxml.jackson.databind.DeserializationFeature;
// import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired 
    StockRepository stockRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverMountCompleted_PackOrder(@Payload MountReqested mountReqested){

        if(!mountReqested.validate()) return;

        System.out.println("$$$$$ listener PackOrder : " + mountReqested.toJson() + "$$$$$");

        if(mountReqested.getOrderStatus().equals("MOUNT_REQUESTED")){

            Stock stock = stockRepository.findByPackType(mountReqested.getOrderPackType());
            if (stock != null){
                // Pack의 보유 갯수를 줄여 준다.
                // 보유 Pack의 개수 order 수량 보다 작은 경우 10000개를 자동으로 재 주문한다.
                if (stock.getPackQty() < mountReqested.getOrderPackQty()){
                    stock.setPackQty(stock.getPackQty()+10000);
                } 

                stock.setPackQty(stock.getPackQty() - mountReqested.getOrderPackQty());

                System.out.println("$$$$$ stock PackType : ["+ stock.getPackType()+"], stock PackQty : [" + stock.getPackQty().toString() + "] $$$$$");
                
                stock.setOrderStatus("PACK_DELIVERY");
                stockRepository.save(stock);
            }else{ 
                // Pack이 없는 경우 10000개를 자동으로 주문한다.
                stock = new Stock();
                BeanUtils.copyProperties(mountReqested, stock);
                stock.setPackType(mountReqested.getOrderPackType());
                stock.setPackQty(10000);
                stock.setPackQty(stock.getPackQty() - mountReqested.getOrderPackQty());
                
                System.out.println("$$$$$ newStock PackType : ["+ stock.getPackType()+"], stock PackQty : [" + stock.getPackQty().toString() + "] $$$$$");
                stock.setOrderStatus("PACK_DELIVERY");
                stockRepository.save(stock);

            }
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverMountCanceled_PackCancel(@Payload MountCancelRequested mountCancelRequested){

        if(!mountCancelRequested.validate()) return;

        System.out.println("$$$$$ listener PackCancel : " + mountCancelRequested.toJson() + "$$$$$");

        if(mountCancelRequested.getOrderStatus().equals("MOUNT_CANCEL_REQUESTED")){
            Stock stock = stockRepository.findByPackType(mountCancelRequested.getOrderPackType());
            if(stock != null){
                // Pack 취소시 개수를 원복 시킨다.
                stock.setPackQty(stock.getPackQty()+mountCancelRequested.getOrderPackQty());
                stock.setOrderStatus("PACK_CANCEL");
                stockRepository.save(stock);
            }
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}