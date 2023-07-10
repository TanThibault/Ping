package fr.epita.assistants;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.request.*;
import fr.epita.assistants.response.*;
import fr.epita.assistants.utils.Users;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.Charset;
import java.nio.file.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.request.CommandRequest;
import fr.epita.assistants.request.CommitRequest;
import fr.epita.assistants.request.NameRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


@Path("/")
public class Endpoints {

    MyIde ide = new MyIde();
    ProjectService ActualService;
    Project Actual;
    @POST
    @Path("/git/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Add(List<PathRequest> list)
    {
        if(Actual== null || ActualService == null || Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.GIT.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        list.forEach(pathRequest -> stringList.add(pathRequest.path));
        var pol = ActualService.execute(Actual, Mandatory.Features.Git.ADD,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/git/commit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Commit(CommitRequest request)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.GIT.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Git.COMMIT,request.getMessage());
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/git/pull")

    @Produces(MediaType.APPLICATION_JSON)
    public Response Pull()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.GIT.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Git.PULL);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/git/push")
    @Produces(MediaType.APPLICATION_JSON)
    public Response Push()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.GIT.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Git.PUSH);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/maven/compile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response compile()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.COMPILE);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/maven/install")
    @Produces(MediaType.APPLICATION_JSON)
    public Response mvn_install()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.INSTALL);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/maven/exec")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response exec(List<CommandRequest> req)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        req.forEach(pathRequest -> stringList.add(pathRequest.getCommand()));
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.COMPILE,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/maven/tree")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tree(List<CommandRequest> req)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        req.forEach(pathRequest -> stringList.add(pathRequest.getCommand()));
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.TREE,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }

    @GET
    @Path("/maven/clean")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clean()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.CLEAN);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/maven/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.TEST);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/maven/package")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pack()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())))
        {
            System.out.println(Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.MAVEN.toString())));
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Maven.PACKAGE);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @GET
    @Path("/npm/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Response init()
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.NPM.toString())))
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Npm.INIT);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/npm/exe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response exe(List<CommandRequest> requests)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.NPM.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        requests.forEach(pathRequest -> stringList.add(pathRequest.getCommand()));
        var pol = ActualService.execute(Actual, Mandatory.Features.Npm.EXE,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/npm/install")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response node_install(List<CommandRequest> requests)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.NPM.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        requests.forEach(pathRequest -> stringList.add(pathRequest.getCommand()));
        var pol = ActualService.execute(Actual, Mandatory.Features.Npm.INSTALL,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }
    @POST
    @Path("/npm/uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uninstall(List<CommandRequest> requests)
    {
        if(Actual== null || ActualService == null|| Actual.getAspects().stream().noneMatch(aspect -> aspect.getType().toString().equals(Mandatory.Aspects.NPM.toString())))
        {
            return Response.status(400).build();
        }
        List<String> stringList = new ArrayList<>();
        requests.forEach(pathRequest -> stringList.add(pathRequest.getCommand()));
        var pol = ActualService.execute(Actual, Mandatory.Features.Npm.REMOVE,stringList);
        if(pol.isSuccess())
        {
            return Response.status(200).entity(pol).build();
        }
        else
            return Response.status(404).entity(pol).build();
    }

    @GET
    @Path("/cleanup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cleanup()
    {
        if(Actual== null || ActualService == null)
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Any.CLEANUP);
        if (pol.isSuccess()) {
            return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
        } else
            return Response.status(404).build();
    }

    @GET
    @Path("/dist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dist()
    {
        if(Actual== null || ActualService == null)
        {
            return Response.status(400).build();
        }
        var pol = ActualService.execute(Actual, Mandatory.Features.Any.DIST);
        if (pol.isSuccess()) {
            return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
        } else
            return Response.status(404).build();
    }

    @POST
    @Path("/start_ide")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response start_ide(PathRequest req)
    {
        if(ide.getMongoClient()==null)
        {
            return Response.status(403).build();
        }
        ActualService = MyIde.init(new MyIde.Configuration(java.nio.file.Path.of(req.getPath()),java.nio.file.Path.of("poui")));
        return Response.status(200).build();
    }
    @POST
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response start_project(PathRequest req)
    {
        if(ide.getMongoClient()==null)
        {
            return Response.status(403).build();
        }
        Actual = ActualService.load(java.nio.file.Path.of(req.getPath()));
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }

    ///////////////////////// Node Service ////////////////////////////////

    @POST
    @Path("/nodeService/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_node(UpdateNodeRequest req) {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        ActualService.getNodeService().update(new NodeImplementation(java.nio.file.Path.of(req.getNode())), req.getFrom(), req.getTo(), req.getInsertedContent().getBytes(StandardCharsets.UTF_8));
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }

    @POST
    @Path("/nodeService/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete_node(DeleteNodeRequest req) {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        ActualService.getNodeService().delete(new NodeImplementation(java.nio.file.Path.of(req.getNode())));
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }

    @POST
    @Path("/nodeService/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create_node(CreateNodeRequest req) {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        ActualService.getNodeService().create(new NodeImplementation(java.nio.file.Path.of(req.getFolder())), req.getName(), Node.Types.valueOf(req.getType()));
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }

    @POST
    @Path("/nodeService/move")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response move_node(MoveNodeRequest req) {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        ActualService.getNodeService().move(new NodeImplementation(java.nio.file.Path.of(req.getNodeToMove())), new NodeImplementation(java.nio.file.Path.of(req.getDestinationFolder())));
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }

    @POST
    @Path("/nodeService/read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response read_node(ReadNodeRequest req) throws IOException {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        byte[] encoded = Files.readAllBytes(java.nio.file.Path.of(req.getNode()));
        String content = new String(encoded, StandardCharsets.UTF_8);
        return Response.status(200).entity(new ReadResponse(content)).build();
    }

    @GET
    @Path("/nodeService/root")
    @Produces(MediaType.APPLICATION_JSON)
    public Response root_node(){
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        return Response.status(200).entity(new RootResponse(Actual.getRootNode().getPath().toString())).build();
    }

    ///////////////////////// Node Service ////////////////////////////////

    ///////////////////////// Tmp Node Service ////////////////////////////

    /*@POST
    @Path("/nodeService/create_tmp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create_tmp(CreateTmpNodeRequest req) {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        ActualService.getTmpNodeService().create(new NodeImplementation(java.nio.file.Path.of("/")), req.getPath(), Node.Types.FILE);
        return Response.status(200).entity(NodeResponse.response(Actual.getRootNode())).build();
    }*/

    ///////////////////////// Tmp Node Service ////////////////////////////

    ///////////////////////// User Service ////////////////////////////////

    @POST
    @Path("/connection")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response connection(NameRequest req) {
        ide.connect(req.getPseudo());
        return Response.status(200).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response users() {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        MongoDatabase db = ide.getMongoClient().getDatabase("ide_user");

        UsersResponse response = new UsersResponse(new ArrayList<Users>());

        FindIterable<Document> found = db.getCollection("user").find();

        for (var u : found) {
            Users user = new Users(u.get("_id").toString(), u.get("name").toString(), u.get("status").toString());
            response.users.add(user);
        }

        return Response.status(200).entity(response).build();
    }

    @GET
    @Path("/leave")
    public Response leave() {
        ide.leave();
        return Response.status(200).build();
    }

    @POST
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response status(StatusRequest req) {
        ide.change_status(Status.valueOf(req.getStatus()));
        return Response.status(200).build();
    }

    @GET
    @Path("/nb_user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response nb_user() {
        if (ide.getMongoClient() == null) {
            return Response.status(403).build();
        }
        Document doc = new Document();
        doc.put("status", Status.HELP);
        long nb = ide.getMongoClient().getDatabase("ide_user").getCollection("user").countDocuments(doc);
        return Response.status(200).entity(new nbResponse(nb)).build();
    }

    ///////////////////////// User Service ////////////////////////////////
}
