package chargingstation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
// import java.util.List;
// import java.util.Date;

@Entity
@Table(name="Stock_table")
public class Stock {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String packType;
    private Integer packQty;
    private Long orderId;
    private String orderStatus;

    @PostPersist
    public void onPostPersist(){
        PackDelivered packDelivered = new PackDelivered();
        BeanUtils.copyProperties(this, packDelivered);
        packDelivered.setOrderPackType(this.packType);
        packDelivered.publishAfterCommit();

        System.out.println("$$$$$ Stock onPostPersist $$$$$"); 
        System.out.println("$$$$$ packDelivered : [" + packDelivered.toJson()+"] $$$$$");       
    }


    @PostUpdate
    public void onPostUpdate(){
        if (this.getOrderStatus().equals("PACK_DELIVERY")){
            PackDelivered packDelivered = new PackDelivered();
            BeanUtils.copyProperties(this, packDelivered);
            packDelivered.setOrderPackType(this.packType);
            packDelivered.publishAfterCommit();

            System.out.println("$$$$$ Stock onPostUpdate $$$$$"); 
            System.out.println("$$$$$ packDelivered : [" + packDelivered.toJson()+"] $$$$$");       
        }else{
            PackDeliveryCanceled packDeliveryCanceled = new PackDeliveryCanceled();
            BeanUtils.copyProperties(this, packDeliveryCanceled);
            packDeliveryCanceled.setOrderPackType(this.packType);
            packDeliveryCanceled.publishAfterCommit();
    
            System.out.println("$$$$$ Stock onPostUpdate  $$$$$");
            System.out.println("$$$$$ packDeliveryCanceled : [" + packDeliveryCanceled.toJson()+"] $$$$$");            
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public Integer getPackQty() {
        return packQty;
    }

    public void setPackQty(Integer packQty) {
        this.packQty = packQty;
    }

    
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}