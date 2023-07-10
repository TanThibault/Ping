package fr.epita.assistants.utils;

import fr.epita.assistants.Status;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Users {
    public String id;
    public String name;
    public String status;
}
