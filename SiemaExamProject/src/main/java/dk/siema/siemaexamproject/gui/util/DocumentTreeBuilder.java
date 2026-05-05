package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.gui.ScannerViewController.TreeNode;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentTreeBuilder {

    private final Map<String, TreeItem<TreeNode>> nodeMap = new HashMap<>();

    public TreeItem<TreeNode> getNode(FileEntity file) {
        return (file == null) ? null : nodeMap.get(file.getFilePath());
    }

    public TreeItem<TreeNode> build(List<Document> documents) {

        nodeMap.clear();

        TreeItem<TreeNode> root =
                new TreeItem<>(new TreeNode("BOX", null, -1));

        root.setExpanded(true);

        for (int d = 0; d < documents.size(); d++) {

            Document doc = documents.get(d);

            TreeItem<TreeNode> docNode =
                    new TreeItem<>(new TreeNode("Document " + (d + 1), null, d));

            for (int f = 0; f < doc.getPages().size(); f++) {

                FileEntity file = doc.getPages().get(f);

                String label = "File " + (f + 1);
                if (file.isBarcode()) {
                    label += " (BARCODE)";
                }

                TreeItem<TreeNode> fileNode =
                        new TreeItem<>(new TreeNode(label, file, d));

                nodeMap.put(file.getFilePath(), fileNode);

                docNode.getChildren().add(fileNode);
            }

            root.getChildren().add(docNode);
        }

        return root;
    }
}