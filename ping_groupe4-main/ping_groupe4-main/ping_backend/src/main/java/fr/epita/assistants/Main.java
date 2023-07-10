package fr.epita.assistants;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import org.bson.Document;

import javax.print.Doc;

import java.nio.file.Path;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;


public class Main {
    public static void main(String[] args) {
        //MyIde k = new MyIde();
        //var projectService = MyIde.init(new MyIde.Configuration(Path.of("./"),Path.of("./")));
        //var pr = projectService.load(Path.of("./"));
        //projectService.execute(pr, Mandatory.Features.Maven.COMPILE);

        //MyIde k = new MyIde("nicolas le boss");
        //System.out.println(k.id);
        //k.change_status(Status.BREAK);
    }
}