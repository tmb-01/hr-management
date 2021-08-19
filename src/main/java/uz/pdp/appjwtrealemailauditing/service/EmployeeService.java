package uz.pdp.appjwtrealemailauditing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.entity.*;
import uz.pdp.appjwtrealemailauditing.entity.enums.RoleName;
import uz.pdp.appjwtrealemailauditing.entity.enums.TaskStatus;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.payload.EmpResponse;
import uz.pdp.appjwtrealemailauditing.payload.SalaryDto;
import uz.pdp.appjwtrealemailauditing.repository.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    SalaryHistoryRepository salaryHistoryRepository;
    @Autowired
    TurnstileRepository turnstileRepository;
    @Autowired
    TaskRepository taskRepository;

    public ApiResponse findAllEmployees() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {

            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            boolean checkRole = false;
            for (Role role : roles) {
                if (role.getName().name().equals("DIRECTOR") || role.getName().name().equals("HR_MANAGER")) {
                    checkRole = true;
                    break;
                }
            }
            if (!checkRole)
                return new ApiResponse("You don't have access for this operation", false);

            Optional<Role> optionalRole = roleRepository.findByName(RoleName.ROLE_WORKER);
            if (!optionalRole.isPresent())
                return new ApiResponse("Role not found!", false);

            Set<Role> roleSet = new HashSet<>();
            roleSet.add(optionalRole.get());

            List<User> employeeList = userRepository.findAllByRolesIn(Collections.singleton(roleSet));

            return new ApiResponse("Success!", true, employeeList);
        }

        return new ApiResponse("Authorization empty!", false);
    }


    public EmpResponse findOneByData(UUID id, Timestamp start, Timestamp finish) {

        LocalDateTime startLocal = start.toLocalDateTime();
        LocalDateTime finishLocal = finish.toLocalDateTime();

        Optional<User> optionalEmployee = userRepository.findById(id);
        if (!optionalEmployee.isPresent())
            return new EmpResponse("Such employee id not found!", false);

        Set<Role> roles = optionalEmployee.get().getRoles();
        boolean checkEmployeeRole = false;
        for (Role role : roles) {
            if (role.getName().name().equals("EMPLOYEE")) {
                checkEmployeeRole = true;
                break;
            }
        }

        List<Turnstile> turniketList =
                turnstileRepository.findAllByCreatedByAndEnterDateTimeAndExitDateTimeBefore(id, startLocal, finishLocal);

        if (turniketList.isEmpty())
            return new EmpResponse("Data not found!", false);

        EmpResponse empResponse = new EmpResponse();
        empResponse.setTurniketList(turniketList);

        if (checkEmployeeRole) {
            List<Task> taskList = taskRepository.findAllByStatusAndResponsibleId(TaskStatus.DONE, id);
            empResponse.setTaskList(taskList);
        }

        return empResponse;
    }

    public ApiResponse payMonthly(SalaryDto salaryDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            boolean checkRole = false;
            for (Role role : roles) {
                if (role.getName().name().equals("DIRECTOR") || role.getName().name().equals("HR_MANAGER")) {
                    checkRole = true;
                    break;
                }
            }
            if (!checkRole)
                return new ApiResponse("You don't have access for this operation", false);

            Optional<User> optionalEmployee = userRepository.findById(salaryDto.getEmployeeId());
            if (!optionalEmployee.isPresent())
                return new ApiResponse("Such Employee was not found!", false);

            Salary salaryHistory = new Salary();
            salaryHistory.setWorker(optionalEmployee.get());
            salaryHistory.setSalaryAmount(salaryDto.getSalaryAmount());
            salaryHistory.setWorkStartDate(salaryDto.getWorkStartDate());
            salaryHistory.setWorkEndDate(salaryDto.getWorkEndDate());

            salaryHistoryRepository.save(salaryHistory);

            return new ApiResponse("Salary Saved! To: " + optionalEmployee.get().getFirstName(), true);
        }
        return new ApiResponse("Authorization empty!", false);
    }

    public ApiResponse getSalariesByMonth(String year, Integer monthNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            boolean checkRole = false;
            for (Role role : roles) {
                if (role.getName().name().equals("ROLE_DIRECTOR") || role.getName().name().equals("ROLE_HR_MANAGER")) {
                    checkRole = true;
                    break;
                }
            }
            if (!checkRole)
                return new ApiResponse("You don't have access for this operation", false);

            String month = monthNumber + "";
            if (monthNumber < 10)
                month = "0" + monthNumber;

            String full = year + "-" + month + "-01 05:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(full, formatter);

            Timestamp start = Timestamp.valueOf(dateTime);
            List<Salary> salaryHistoryList = salaryHistoryRepository.findAllByWorkStartDate(start);
            if (salaryHistoryList.size() == 0)
                return new ApiResponse("Salary list empty!", false);

            return new ApiResponse("Success!", true, salaryHistoryList);
        }
        return new ApiResponse("Authorization empty!", false);
    }

    public ApiResponse getSalariesByUserId(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();

            boolean checkRole = false;
            for (Role role : roles) {
                if (role.getName().name().equals("ROLE_DIRECTOR") || role.getName().name().equals("ROLE_HR_MANAGER")) {
                    checkRole = true;
                    break;
                }
            }
            if (!checkRole)
                return new ApiResponse("You don't have access for this operation", false);
            List<Salary> salaryHistoryList = salaryHistoryRepository.findAllByWorkerId(id);
            if (salaryHistoryList.size() == 0)
                return new ApiResponse("Such employee did not get salary!", false);

            return new ApiResponse("Success!", true, salaryHistoryList);
        }
        return new ApiResponse("Authorization empty!", false);
    }
}
