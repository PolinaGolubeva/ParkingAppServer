package dbservice.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import exceptions.ModelException;
import java.util.Date;

public class Order {
    private Long id;
    private Long parkingId;
    private String carNumber;
    private Date start;
    private Date finish;
    private String paymentInfo;

    public Order(Long id, Long parkingId, String carNumber,
                 Long start, Long finish, String paymentInfo) {
        this.id = id;
        this.parkingId = parkingId;
        this.carNumber = carNumber;
        this.start = new Date(start);
        this.finish = new Date(finish);
        this.paymentInfo = paymentInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) throws ModelException {
        if (this.id == null)
            this.id = id;
        else
            throw new ModelException("Can't change id of current order");
    }

    public Long getParkingId() {
        return parkingId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public Date getStart() {
        return start;
    }

    public Date getFinish() {
        return finish;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public String toString() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }

    public static Order fromJson(String json) {
        try {
            return new Gson().fromJson(json, Order.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Illegal string format");
        }
        return null;
    }
}
