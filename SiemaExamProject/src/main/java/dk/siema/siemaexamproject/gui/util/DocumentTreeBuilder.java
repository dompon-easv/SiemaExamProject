package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.gui.ScannerViewController.TreeNode;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;

public class DocumentTreeBuilder {

    public static TreeItem<TreeNode> build(ObservableList<Document> documents) {

        TreeItem<TreeNode> root =
                new TreeItem<>(new TreeNode("BOX", null));
        root.setExpanded(true);

        // Listen to documents being added
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

    private static TreeItem<TreeNode> createDocumentNode(Document doc, int docIndex) {

        TreeItem<TreeNode> docNode =
                new TreeItem<>(new TreeNode("Document " + docIndex, null));

        // 🔥 Listen to pages inside document
        doc.getPages().addListener((ListChangeListener<FileEntity>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (FileEntity file : change.getAddedSubList()) {

                        String label = "File " + (docNode.getChildren().size() + 1);

                        if (file.isBarcode()) {
                            label += " (BARCODE)";
                        }

                        docNode.getChildren().add(
                                new TreeItem<>(new TreeNode(label, file))
                        );
                    }
                }
            }
        });

        return docNode;
    }
}