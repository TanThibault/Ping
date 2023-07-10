package fr.epita.assistants.myide.domain.entity;

import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.NodeServiceImplementation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FeatureImplementation implements Feature {
    private final Type type;
    public FeatureImplementation(Type type)
    {
        this.type = type;
    }

    /////////////////////////////////////////////////////

    static void indexDocs(IndexWriter writer, File file) throws IOException {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (String filename : files) {
                        indexDocs(writer, new File(file, filename));
                    }
                }
            } else {
                try (FileReader fileReader = new FileReader(file)) {
                    Document doc = new Document();
                    doc.add(new StringField("path", file.getPath(), Field.Store.YES));
                    doc.add(new TextField("contents", fileReader));
                    writer.addDocument(doc);
                }
            }
        }
    }

    /////////////////////////////////////////////////////

    @Override
    public @NotNull ExecutionReport execute(final Project project, final Object... params) {
        StringBuilder report = new StringBuilder();
        try {
            if(type instanceof Features.Maven)
            {
                Process p;
                if(type==Features.Maven.COMPILE)
                {
                    p = Runtime.getRuntime().exec("mvn compile",null,project.getRootNode().getPath().toFile());
                } else if (type==Features.Maven.EXEC) {
                    StringBuilder res = new StringBuilder();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> list = (ArrayList<Object>) params[0];
                        for(Object param:list)
                        {
                            res.append(" ").append(param.toString());
                        }
                    }
                    p = Runtime.getRuntime().exec("mvn exec:java"+res,null,project.getRootNode().getPath().toFile());
                } else if (type==Features.Maven.CLEAN) {
                    p = Runtime.getRuntime().exec("mvn clean",null,project.getRootNode().getPath().toFile());
                } else if (type==Features.Maven.INSTALL) {
                    p = Runtime.getRuntime().exec("mvn install",null,project.getRootNode().getPath().toFile());
                } else if (type==Features.Maven.TEST) {
                    p = Runtime.getRuntime().exec("mvn test",null,project.getRootNode().getPath().toFile());
                } else if (type==Features.Maven.PACKAGE) {
                    p = Runtime.getRuntime().exec("ls",null,project.getRootNode().getPath().toFile());
                } else {
                    StringBuilder res = new StringBuilder();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> list = (ArrayList<Object>) params[0];
                        for(Object param:list)
                        {
                            res.append(" ").append(param.toString());
                        }
                    }
                    p = Runtime.getRuntime().exec("mvn dependency:tree" +res,null,project.getRootNode().getPath().toFile());
                }
                int exit = p.waitFor();
                if(exit!=0)
                {
                    BufferedReader is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = is.readLine(  )) != null)
                        report.append(line).append("\n");
                    return new MyExecutionReport(false,report.toString());
                }
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = is.readLine(  )) != null)
                    report.append(line).append("\n");
            } else if (type instanceof Features.Npm) {
                Process p;
                if(type == Features.Npm.EXE)
                {
                    StringBuilder res = new StringBuilder();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> list = (ArrayList<Object>) params[0];
                        for(Object param:list)
                        {
                            res.append(" ").append(param.toString());
                        }
                    }
                    p = Runtime.getRuntime().exec("node"+res,null,project.getRootNode().getPath().toFile());
                } else if (type == Features.Npm.INIT) {
                    p = Runtime.getRuntime().exec("npm install" ,null,project.getRootNode().getPath().toFile());
                }
                else if (type == Features.Npm.INSTALL) {
                    StringBuilder res = new StringBuilder();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> list = (ArrayList<Object>) params[0];
                        for(Object param:list)
                        {
                            res.append(" ").append(param.toString());
                        }
                    }
                    p = Runtime.getRuntime().exec("npm install"+res ,null,project.getRootNode().getPath().toFile());
                }
                else {
                    StringBuilder res = new StringBuilder();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> list = (ArrayList<Object>) params[0];
                        for(Object param:list)
                        {
                            res.append(" ").append(param.toString());
                        }
                    }
                    p = Runtime.getRuntime().exec("npm uninstall"+res ,null,project.getRootNode().getPath().toFile());
                }
                int exit = p.waitFor();
                if(exit!=0)
                {
                    BufferedReader is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = is.readLine(  )) != null)
                        report.append(line).append("\n");
                    return new MyExecutionReport(false,report.toString());
                }
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = is.readLine(  )) != null)
                    report.append(line).append("\n");
            }
            else if (type instanceof Features.Git) {
                Git repo = Git.open(project.getRootNode().getPath().toFile());
                if(type==Features.Git.ADD)
                {
                    AddCommand add = repo.add();
                    if(params[0] instanceof List<?>)
                    {
                        List<Object> p = (ArrayList<Object>) params[0];
                        for(Object param:p)
                        {
                            add.addFilepattern(param.toString());
                        }
                    }
                    else
                    {
                        for (Object param : params) {
                            add.addFilepattern(param.toString());
                        }
                    }
                } else if (type==Features.Git.PULL) {
                    var k = repo.pull().call();
                    report = new StringBuilder(k.toString());
                } else if(type==Features.Git.PUSH){
                    var k = repo.push().call();
                    AtomicReference<Boolean> success = new AtomicReference<>(true);
                    k.forEach(pushResult -> {
                        for(var p :pushResult.getRemoteUpdates())
                        {
                            if(p.getStatus() == RemoteRefUpdate.Status.UP_TO_DATE && success.get())
                            {
                                success.set(false);
                            }
                        }
                    });
                    repo.close();
                    return new MyExecutionReport(success.get(),k.toString());
                } else {
                    report = new StringBuilder(repo.commit().setMessage(params[0].toString()).call().toString());
                }
                repo.close();
            }
            else {
                if (type==Features.Any.DIST)
                {
                    project.getFeature(Features.Any.CLEANUP).get().execute(project);
                    File fileToZip = project.getRootNode().getPath().toFile();
                    FileOutputStream fos = new FileOutputStream(project.getRootNode().getPath()+".zip");
                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    zipFile(fileToZip, fileToZip.getName(), zipOut);
                    zipOut.close();
                    fos.close();
                } else if (type==Features.Any.CLEANUP) {
                    File[] files = project.getRootNode().getPath().toFile().listFiles((d, s) -> s.equals(".myideignore"));
                    if(files!=null && files.length>0)
                    {
                        Set<Path> to_ignore = new HashSet<>();
                        try
                        {
                            FileReader fr = new FileReader(files[0]);
                            BufferedReader br = new BufferedReader(fr);
                            String line;
                            while((line = br.readLine()) != null)
                            {
                                to_ignore.add(Path.of(line));
                            }
                            fr.close();
                            br.close();
                        }
                        catch(IOException e)
                        {
                            return new MyExecutionReport(false,e.toString());
                        }

                        Queue<Node> queue = new LinkedBlockingQueue<>();
                        queue.add(project.getRootNode());
                        NodeService service = new NodeServiceImplementation(project.getRootNode());
                        while(!queue.isEmpty())
                        {
                            Node node = queue.remove();
                            boolean remove =  false;
                            for(Path str: to_ignore)
                            {
                                if(node.getPath().endsWith(str))
                                {
                                    remove = true;

                                }
                            }
                            if(remove)
                            {
                                service.delete(node);
                                //System.out.println("je delete le fichier/dossier"+node.getPath().toFile().getName());
                            }
                            else {
                                if (node.isFolder()) {
                                    queue.addAll(node.getChildren());
                                }
                            }

                        }
                    }
                }
                else {
                    //lucene(input:string to find ; output:bool if present)
                    String inputText = params[0].toString();
                    Analyzer analyzer = new StandardAnalyzer();

		            Directory indexDirectory = FSDirectory.open(project.getRootNode().getPath());
		            IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(analyzer));
                    
                    //Add fields to doc using FileReader, 1 document for each file
                    
                    File rootDirectory = new File(project.getRootNode().getPath().toString());
                    File[] files = rootDirectory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile()) {
                                    FileReader fileReader = new FileReader(file);
                                    Document doc = new Document();
                                    doc.add(new StringField("path", file.getPath(), Field.Store.YES));
                                    doc.add(new TextField("contents", fileReader));
                                    indexWriter.addDocument(doc);
                                }
                            }
                        }
                    Document doc = new Document();
                    indexWriter.addDocument(doc);

                    ////////

                    indexWriter.close();

		            Query query = new QueryParser("contents", analyzer).parse(inputText);
                    IndexReader indexReader = DirectoryReader.open(indexDirectory);
                    IndexSearcher searcher = new IndexSearcher(indexReader);
                    TopDocs topDocs = searcher.search(query, 10);
    
                    return new MyExecutionReport(topDocs.totalHits.value > 0,topDocs.totalHits.toString());
                }
            }
        }catch (Exception e)
        {
            System.err.println("problem pour exe " +e);
            return new MyExecutionReport(false,e.toString());
        }
        return new MyExecutionReport(true, report.toString());
    }

    @Override
    public @NotNull Type type() {
        return type;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class MyExecutionReport implements ExecutionReport {

        private boolean success;
        public String result;

        @Override
        public boolean isSuccess() {
            return success;
        }

    }
    private static void zipFile(File fileToZip, String fileName,ZipOutputStream zipOut) throws IOException
    {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
    public enum Features {
        ;
        /**
         * Features for all projects.
         */
        public enum Any implements Type {
            /**
             * Remove all nodes of trash files.
             * Trash files are listed, line by line,
             * in a ".myideignore" file at the root of the project.
             */
            CLEANUP,

            /**
             * Remove all trash files and create a zip archive.
             * Archive name must be the same as the project name (root node name).
             */
            DIST,

            /**
             * Fulltext search over project files.
             */
            SEARCH
        }

        /**
         * Features for the git project type.
         */
        public enum Git implements Type {
            /**
             * Git pull, fast-forward if possible.
             */
            PULL,

            /**
             * Git add.
             */
            ADD,

            /**
             * Git commit.
             */
            COMMIT,

            /**
             * Git push (no force).
             */
            PUSH
        }
        public enum Npm implements Feature.Type {
            INSTALL,
            REMOVE,
            EXE,
            INIT
        }

        /**
         * Features for the maven project type.
         * All commands are executed in the project root.
         */
        public enum Maven implements Type {
            /**
             * mvn compile
             */
            COMPILE,

            /**
             * mvn clean
             */
            CLEAN,

            /**
             * mvn test
             */
            TEST,

            /**
             * mvn package
             */
            PACKAGE,

            /**
             * mvn install
             */
            INSTALL,

            /**
             * mvn exec:java
             */
            EXEC,

            /**
             * mvn dependency:tree
             */
            TREE
        }
    }

}