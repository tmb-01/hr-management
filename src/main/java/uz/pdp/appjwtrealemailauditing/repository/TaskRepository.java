package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtrealemailauditing.entity.Task;
import uz.pdp.appjwtrealemailauditing.entity.enums.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByStatusAndResponsibleId(TaskStatus status, UUID responsible_id);

}
