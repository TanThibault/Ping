package fr.epita.assistants;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.myide.domain.service.ProjectServiceImplementation;
import fr.epita.assistants.utils.Given;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.nio.file.Path;
/**
 * Starter class, we will use this class and the init method to get a
 * configured instance of {@link ProjectService}.
 */
@Given(overwritten = false)
@ApplicationScoped
public class MyIde {

    public ObjectId id;
    private String uri = "mongodb+srv://admin:annabella@cluster0.ub3vsnr.mongodb.net/?retryWrites=true&w=majority";
    private MongoClient mongoClient;
    private boolean isAdmin = false;


    public void connect (String name_)
    {
        mongoClient = MongoClients.create(uri);
        MongoDatabase db = mongoClient.getDatabase("ide_user");
        Document query = new Document();
        query.put("name",name_);
        Document found = db.getCollection("user").find(query).first();
        if(found==null)
        {
            query.put("status",Status.CONNECTED);
            db.getCollection("user").insertOne(query);
        }
        id = (ObjectId) db.getCollection("user").find(query).first().get("_id");
    }

    public void set_admin(String auth)
    {
        if (auth == "foo")
            isAdmin = true;
        else
        {
            System.err.println("Wrong password");
        }
    }

    public void change_name(String name)
    {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase db = mongoClient.getDatabase("ide_user");
            Document query = new Document();
            query.put("_id",id);
            Document newDoc = new Document();
            newDoc.put("name",name);
            Document update = new Document();
            update.put("$set",newDoc);
            db.getCollection("user").updateOne(query,update);
        }
    }

    public void change_name_admin(ObjectId targetId, String name)
    {
        if (isAdmin)
        {
            try (MongoClient mongoClient = MongoClients.create(uri)) 
            {
                MongoDatabase db = mongoClient.getDatabase("ide_user");
                Document query = new Document();
                query.put("_id",targetId);
                Document newDoc = new Document();
                newDoc.put("name",name);
                Document update = new Document();
                update.put("$set",newDoc);
                db.getCollection("user").updateOne(query,update);
            }
        }
        else
        {
            System.err.println("You do not have the permissions to do that");
        }
    }

    public void change_status(Status status)
    {
            MongoDatabase db = mongoClient.getDatabase("ide_user");
            Document query = new Document();
            query.put("_id",id);
            Document newDoc = new Document();
            newDoc.put("status",status);
            Document update = new Document();
            update.put("$set",newDoc);
            db.getCollection("user").updateOne(query,update);
    }

    public void leave()
    {
        MongoDatabase db = mongoClient.getDatabase("ide_user");
        Document query = new Document();
        query.put("_id",id);
        db.getCollection("user").deleteOne(query);
    }

    /*public void remove_admin(ObjectId targetId)
    {
        if (isAdmin)
        {
            try (MongoClient mongoClient = MongoClients.create(uri)) 
            {
                MongoDatabase db = mongoClient.getDatabase("ide_user");
                if(found!=null)
                {
                    db.getCollection("user").remove("_id",targetId);
                }
            }
        }
        else
        {
            System.err.println("You do not have the permissions to do that");
            //Error.println("You do not have the permissions to do that");
        }
    }*/

    public String getUri() {
        return uri;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setUri(String uri) {
        this.uri = uri;
        mongoClient.close();
        mongoClient = MongoClients.create(uri);
    }

    /**
     * Init method. It must return a fully functional implementation of {@link ProjectService}.
     *
     * @return An implementation of {@link ProjectService}.
     */
    public static ProjectService init(final Configuration configuration) {
        ProjectService ps = new ProjectServiceImplementation(configuration.indexFile, configuration.tempFolder);
        //ps.load(indexFile);   
        return ps;
        //J'ai envie de pleurer
        // throw new UnsupportedOperationException("FIXME");
    }

    /**
     * Record to specify where the configuration of your IDE
     * must be stored. Might be useful for the search feature.
     */
    public record Configuration(Path indexFile,
                                Path tempFolder) {
    }

}
