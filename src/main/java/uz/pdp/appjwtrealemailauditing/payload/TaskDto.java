package uz.pdp.appjwtrealemailauditing.payload;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TaskDto {

    private String title;
    private String body;
    private Date deadLine;
    private UUID responsibleId;
}
