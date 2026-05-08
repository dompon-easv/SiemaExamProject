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

    public TreeItem<TreeNode> build(List<Document> documents, String boxId) {

        nodeMap.clear();

        String rootLabel = "📦 Box";

        if (boxId != null && !boxId.isBlank()) {
            rootLabel += " - " + boxId;
        }

        TreeItem<TreeNode> root =
                new TreeItem<>(new TreeNode(rootLabel, null, -1));

        root.setExpanded(true);

        for (int d = 0; d < documents.size(); d++) {

            Document doc = documents.get(d);

            TreeItem<TreeNode> docNode =
                    new TreeItem<>(new TreeNode("📁 Document " + (d + 1), null, d));

            for (int f = 0; f < doc.getPages().size(); f++) {

                FileEntity file = doc.getPages().get(f);

                String fileName = new java.io.File(file.getFilePath()).getName();

                String label = "🧾 " + String.format("%02d - %s", f + 1, fileName);
                if (file.isBarcode()) {
                    label += " 🔍";
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