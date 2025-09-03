package co.com.crediya.requests.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String documentNumber;
    private String phoneNumber;
    private Float baseSalary;
    private String roleName;
    private Integer roleId;
}
