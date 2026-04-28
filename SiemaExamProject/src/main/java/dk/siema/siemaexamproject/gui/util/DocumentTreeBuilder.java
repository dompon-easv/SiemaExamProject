package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.gui.ScannerViewController.TreeNode;
import javafx.scene.control.TreeItem;

import java.util.List;

public class DocumentTreeBuilder {

    public static TreeItem<TreeNode> build(List<Document> documents) {

        TreeItem<TreeNode> root =
                new TreeItem<>(new TreeNode("BOX", null));
        root.setExpanded(true);

        int docIndex = 1;

        for (Document doc : documents) {

            TreeItem<TreeNode> docNode =
                    new TreeItem<>(new TreeNode("Document " + docIndex++, null));

            int pageIndex = 1;

            for (FileEntity file : doc.getPages()) {

                String label = "File " + pageIndex++;

                if (file.isBarcode()) {
                    label += " (BARCODE)";
                }

                docNode.getChildren().add(
                        new TreeItem<>(new TreeNode(label, file))
                );
            }

            root.getChildren().add(docNode);
        }

        return root;
    }
}