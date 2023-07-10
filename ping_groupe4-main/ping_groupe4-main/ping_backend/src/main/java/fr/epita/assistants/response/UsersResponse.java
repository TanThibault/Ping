package fr.epita.assistants.response;

import java.util.List;

import lombok.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.utils.Users;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersResponse {
    public List<Users> users;
}