package servlet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    String orderReference;
    String customerName;
    String customerPhone;
    Double totalBill;
    Date dateOrder;
}
