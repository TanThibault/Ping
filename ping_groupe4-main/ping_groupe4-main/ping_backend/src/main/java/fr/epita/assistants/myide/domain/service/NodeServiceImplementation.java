package fr.epita.assistants.myide.domain.service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeImplementation;
import fr.epita.assistants.utils.Exceptions;
import fr.epita.assistants.utils.ThrowingRunnable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NodeServiceImplementation implements NodeService {
    public Node root;
    public NodeServiceImplementation(Node p) {
        this.root = p;
    }
    public Node getRoot() {
        return root;
    }
    @Override
    public Node update(final Node node,
                       final int from,
                       final int to,
                       final byte[] insertedContent) {
        if (node.isFolder()) {
            throw new RuntimeException(node + "is a folder");
        }
        try {
            Path nodePath = node.getPath();
            RandomAccessFile file = new RandomAccessFile(nodePath.toFile(), "rw");
            /* buffer avec tous le file */
            file.seek(from);
            byte[] buffer = new byte[(int) file.length() - from ];
            int r = file.read(buffer);
            int add = from == to || from == to -1 ? 0 : 1;
            byte[] newbuffer = new byte[r - (to - from) + insertedContent.length];
            for (int i = 0; i < insertedContent.length; i++) {
                newbuffer[i] = insertedContent[i];
            }
            int i = insertedContent.length;
            for (int j = to - from; j < r; j ++) {
                newbuffer[i] = buffer[j];
                i++;
            }
            file.seek(from);
            file.write(newbuffer);
            file.close();
            return node;
        }
        catch (Exception e) {
            throw new RuntimeException();
        }

    }
    public boolean deleteChildren(final Node node) {
        Path path = node.getPath();
        File f = path.toFile();
        if (node.isFolder()) {
            List<Node> nodes = node.getChildren();
            for (int i = 0; i < nodes.size(); i++) {
                deleteChildren(nodes.get(i));
            }
            node.getChildren().clear();
        }
        return f.delete();
    }
    @Override
    public boolean delete(final Node node) {
        try {
            Path path = node.getPath();
            File f = path.toFile();
            // parcours de larbre jusqua la a delete
            Queue<Node> fifo = new LinkedList<>();
            fifo.add(this.root);
            boolean found = false;
            while (!fifo.isEmpty() && !found) {
                Node n = fifo.remove();
                //traitement pour voir si c'est le node parent du node a delete
                for (int j = 0; j < n.getChildren().size(); j++) {
                    if (n.getChildren().get(j) == node) {
                        n.getChildren().remove(j);
                        found = true;
                    }
                }
                // si pas found on continue
                if (!found) {
                    for (int i = 0; i < n.getChildren().size(); i++) {
                        fifo.add(n.getChildren().get(i));
                    }
                }
            }
            if (node.isFolder()) {
                return deleteChildren(node);
            }
            return f.delete();
        }
        catch (Exception e) {
            return false;
        }
    }
    @Override
    public Node create(final Node folder,
                       final String name,
                       final Node.Type type) {
        if (folder.isFile()) {
                throw new RuntimeException(folder + "is a file");
        }
        // parcour de l'abre pour trouver le node folder
        Path p = folder.getPath().resolve(name);
        //Path p = Path.of(name);

        //p = Path.of(name);
        File f = p.toFile();
        //check si le truc exist deja ou pas, ajoute juste le node si oui
        if (!f.exists() && type == Node.Types.FOLDER) {
            f.mkdir();
        }
        else if (!f.exists()) {
            try {
                f.createNewFile();
                //Files.createFile(p);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < folder.getChildren().size(); i++) {
            if (folder.getChildren().get(i).getPath() == p){
                return folder.getChildren().get(i);
            }
        }
        Node res = new NodeImplementation(p);
        ((NodeImplementation) folder).addChildren(res);

        //folder.getChildren().add(res);
        return res;
    }
    @Override
    public Node move(final Node nodeToMove,
              final Node destinationFolder) {
        if (nodeToMove == null || destinationFolder == null)
            //Exceptions.mayThrow(ThrowingRunnable<NullPointerException>);
            throw new RuntimeException("Node can not be null");
        if (destinationFolder.isFile()) {
            throw new RuntimeException(destinationFolder.getPath() + "is a file");
        }
        try {
            FileUtils.moveToDirectory(nodeToMove.getPath().toFile(), destinationFolder.getPath().toFile(),
                    false);
            Node n = new NodeImplementation(destinationFolder.getPath().resolve(nodeToMove.getPath().toFile().getName()));
            ((NodeImplementation) destinationFolder).addChildren(n);
            delete(nodeToMove);
            return n;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}