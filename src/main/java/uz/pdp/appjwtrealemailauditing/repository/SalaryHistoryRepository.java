package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.appjwtrealemailauditing.entity.Salary;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Repository
public interface SalaryHistoryRepository extends JpaRepository<Salary, Integer> {

    List<Salary> findAllByWorkerId(UUID employee_id);

    List<Salary> findAllByWorkStartDate(Date workStartDate);



}
