package ru.alligator.bot.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alligator.bot.dto.EmployeeTo;

import java.util.List;

@FeignClient(name = "employee-client", url = "${url.employee}")
public interface EmployeeFeignClient {

    @GetMapping("/api/v1/employees")
    List<EmployeeTo> findEmployees(@RequestParam String query);

}
