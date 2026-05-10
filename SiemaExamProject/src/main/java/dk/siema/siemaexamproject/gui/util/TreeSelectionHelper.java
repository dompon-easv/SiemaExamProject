package dk.siema.siemaexamproject.gui.util;

import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.gui.ScannerViewController;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeSelectionHelper {

    private static final Map<String, Integer> folderRotationCache = new HashMap<>();


    public static void saveFolderRotation(TreeItem<ScannerViewController.TreeNode> folderNode, int angle){
        if (folderNode != null && folderNode.getValue() != null) {
            folderRotationCache.put(folderNode.getValue().label(), angle);
        }
    }

    /*recursively saves the rotation state for the selected node and all folder descendants
    If you click a Box, it updates the Box and all Documents inside with files
    If you click a Document , it only updates files in that Document
     */

    public static void saveFolderHierarchyRotation(TreeItem<ScannerViewController.TreeNode> node, int angle){
        if(node == null || node.getChildren() == null) return;

        //if this specific node is a folder (box/document), save it to cache
        if(node.getValue().file() == null){
            String label = node.getValue().label();
            folderRotationCache.put(label, angle);

            System.out.println("Cached: Cascaded rotation" + angle + "to folder[" + label + "]");
        }

        //recursively dig into the children to update nested documents
        for (TreeItem<ScannerViewController.TreeNode> child : node.getChildren()){
            saveFolderRotation(child, angle);
        }

    }

    //extract exactly which files should be rotate based on what the user select

    public static List<FileEntity> getFilesToRotate(TreeItem<ScannerViewController.TreeNode> selectedNode) {
        List<FileEntity> filesToRotate = new ArrayList<>();
        if (selectedNode == null) {return filesToRotate;}

        ScannerViewController.TreeNode nodeData = selectedNode.getValue();
        //1. the user click on exact file
        if (nodeData != null && nodeData.file() != null) {
            filesToRotate.add(nodeData.file());
            return filesToRotate;
        }

        //2. the user click on a box or document
        for (TreeItem<ScannerViewController.TreeNode> child : selectedNode.getChildren()) {
            filesToRotate.addAll(getFilesToRotate(child));
        }
        return filesToRotate;
    }
    //helper to find the effective rotation of folder (box/document)
    //it looks at the first file of the folder to determine the UI state
    public static int getEffectiveFolderRotation(TreeItem<ScannerViewController.TreeNode> folderNode) {
        if (folderNode == null || folderNode.getValue() == null) {return 0;}

        String label = folderNode.getValue().label();

        // check if we have saved rotation in our memory cache
        if (folderRotationCache.containsKey(label)) {
            return folderRotationCache.get(label);
        }

        //if no explicit rotation is saved, look at the first file

        List<FileEntity> insideFiles = getFilesToRotate(folderNode);
        if (insideFiles != null && !insideFiles.isEmpty()) {
            return insideFiles.get(0).getRotation();
        }
        return 0;
    }
    public static void clearCache() {
        folderRotationCache.clear();
    }

}
