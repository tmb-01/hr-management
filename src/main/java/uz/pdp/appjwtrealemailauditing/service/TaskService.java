package uz.pdp.appjwtrealemailauditing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.entity.Role;
import uz.pdp.appjwtrealemailauditing.entity.Task;
import uz.pdp.appjwtrealemailauditing.entity.User;
import uz.pdp.appjwtrealemailauditing.entity.enums.TaskStatus;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.payload.TaskDto;
import uz.pdp.appjwtrealemailauditing.repository.TaskRepository;
import uz.pdp.appjwtrealemailauditing.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;


    public ApiResponse save(TaskDto taskDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            int role_index = 0;
            for (Role role : roles) {
                switch (role.getName().name()) {
                    case "ROLE_DIRECTOR":
                        role_index = 1;
                        break;
                    case "ROLE_HR_MANAGER":
                    case "ROLE_MANAGER":
                        role_index = 2;
                        break;
                    default:
                        return new ApiResponse("Such role id not found!", false);
                }
            }

            Optional<User> optionalResponsive = userRepository.findById(taskDto.getResponsibleId());
            if (!optionalResponsive.isPresent())
                return new ApiResponse("Such responsive id not found!", false);

            Set<Role> responsiveRoles = optionalResponsive.get().getRoles();

            int role_index2 = 0;

            for (Role responsiveRole : responsiveRoles) {
                switch (responsiveRole.getName().name()) {
                    case "ROLE_DIRECTOR":
                        role_index2 = 1;
                        break;
                    case "ROLE_HR_MANAGER":
                    case "ROLE_MANAGER":
                        role_index2 = 2;
                        break;
                    case "ROLE_EMPLOYEE":
                        role_index2 = 3;
                        break;
                    default:
                        return new ApiResponse("Such responsive role id not found!", false);
                }
            }

            boolean checkSendTaskStatus = false;

            if (role_index == 1 && role_index2 != 1) {
                checkSendTaskStatus = true;
            }

            if (role_index == 2 && role_index2 != 1 && role_index2 != 2) {
                checkSendTaskStatus = true;
            }

            if (!checkSendTaskStatus)
                return new ApiResponse("You can not assign the task to this user!", false);

            Task task = new Task();
            task.setTitle(taskDto.getTitle());
            task.setBody(taskDto.getBody());
            task.setResponsible(optionalResponsive.get());
            task.setStatus(TaskStatus.TO_DO);
            task.setDeadLine(taskDto.getDeadLine());

            taskRepository.save(task);
            return new ApiResponse("Task assigned! From: "
                    + user.getFirstName() + " " + user.getLastName() + " To: "
                    + optionalResponsive.get().getFirstName() + " " + optionalResponsive.get().getLastName(), true);

        }
        return new ApiResponse("Authorization empty!", false);
    }


    public ApiResponse completeTask(Integer id, Integer taskStatus) {

        Optional<Task> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent())
            return new ApiResponse("Such task id not found!", false);

        TaskStatus status = TaskStatus.values()[taskStatus];
        optionalTask.get().setStatus(status);

        taskRepository.save(optionalTask.get());
        return new ApiResponse("Task status updated!", true);
    }

    public ApiResponse checkEmployeeTask(UUID employeeId, Integer status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            boolean checkRoleStatus = false;
            for (Role role : roles) {
                if (role.getName().name().equals("ROLE_DIRECTOR") || role.getName().name().equals("ROLE_HR_MANAGER")) {
                    checkRoleStatus = true;
                    break;
                }
            }

            if (!checkRoleStatus)
                return new ApiResponse("You don't have access for this operation!", false);

            List<Task> taskList = taskRepository.findAllByStatusAndResponsibleId(TaskStatus.values()[status], employeeId);
            if(taskList.size() == 0)
                return new ApiResponse("There is not any task for this data!", false);

            return new ApiResponse("Success!", true, taskList);

        }

        return new ApiResponse("Authorization empty!", false);
    }
}
