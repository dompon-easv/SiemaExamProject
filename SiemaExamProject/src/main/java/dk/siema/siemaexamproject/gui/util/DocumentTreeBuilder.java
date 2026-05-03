package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.gui.ScannerViewController.TreeNode;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

public class DocumentTreeBuilder {

    private final Map<String, TreeItem<TreeNode>> nodeMap = new HashMap<>();

    public TreeItem<TreeNode> getNode(FileEntity file) {
        return (file == null) ? null : nodeMap.get(file.getFilePath());
    }

    public TreeItem<TreeNode> build(ObservableList<Document> documents) {

        nodeMap.clear();

        TreeItem<TreeNode> root = new TreeItem<>(new TreeNode("BOX", null, -1));
        root.setExpanded(true);

        // Listen for new documents
        documents.addListener((ListChangeListener<Document>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Document doc : change.getAddedSubList()) {
                        root.getChildren().add(createDocumentNode(doc, root.getChildren().size() + 1));
                    }
                }
            }
        });

        // Add existing documents
        int docIndex = 1;
        for (Document doc : documents) {
            root.getChildren().add(createDocumentNode(doc, docIndex++));
        }

        return root;
    }

    private TreeItem<TreeNode> createDocumentNode(Document doc, int docIndex) {

        TreeItem<TreeNode> docNode =
                new TreeItem<>(new TreeNode("Document " + docIndex, null, docIndex -1));

        // Listen to pages
        doc.getPages().addListener((ListChangeListener<FileEntity>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (FileEntity file : change.getAddedSubList()) {

                        String label = "File " + (docNode.getChildren().size() + 1);
                        if (file.isBarcode()) {
                            label += " (BARCODE)";
                        }

                        TreeItem<TreeNode> item =
                                new TreeItem<>(new TreeNode(label, file, docIndex -1));

                        nodeMap.put(file.getFilePath(), item);
                        docNode.getChildren().add(item);
                    }
                }
            }
        });

        return docNode;
    }
}