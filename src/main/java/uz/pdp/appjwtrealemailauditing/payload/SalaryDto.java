package uz.pdp.appjwtrealemailauditing.payload;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SalaryDto {

    private UUID employeeId;
    private double salaryAmount;
    private Date workStartDate;
    private Date workEndDate;
}
