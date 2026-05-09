package dk.siema.siemaexamproject.dal.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.siema.siemaexamproject.be.Box;
import dk.siema.siemaexamproject.be.Document;
import dk.siema.siemaexamproject.be.FileEntity;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IBoxDAO;
import dk.siema.siemaexamproject.dal.util.BytesConverter;

import java.sql.*;

public class BoxDAO implements IBoxDAO {


    //called during scan to save file to db immediately
    public void stageFile(FileEntity fileEntity) {
        String sql = "INSERT INTO StagedFiles (reference_id,file_data,is_barcode) VALUES (?,?,?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBytes(1, BytesConverter.uuidToBytes(fileEntity.getReferenceId()));
            pstmt.setBytes(2, fileEntity.getFileData());
            pstmt.setBoolean(3, fileEntity.isBarcode());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DalException("Staging failed: " + e.getMessage());
        }
    }

    public void saveBox(Box box) throws DalException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                //save Box and get its ID
                saveBoxMetadata(conn, box);

                for (Document doc : box.getDocuments()) {
                    //save document linked to boxId
                    int docId = saveDocument(conn, doc, box.getId());

                    for (FileEntity file : doc.getFiles()) {
                        //move from staging or insert normally
                        upsertFile(conn, file, docId);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DalException("Transaction failed: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e){
            throw new DalException("Connection error: " + e.getMessage());
        }

        }

    public void saveBoxMetadata(Connection conn, Box box) throws SQLException {
        // Only insert the INT profile_id
        String sql = "INSERT INTO Boxes (profile_id) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Use setInt here!
            pstmt.setInt(1, box.getProfileId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    box.setId(rs.getInt(1)); // Store the new Box ID
                }
            }
        }
    }

    public int saveDocument(Connection conn, Document document, int dbBoxId) throws SQLException {

        String sql = "INSERT INTO Documents (box_id) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, dbBoxId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    document.setId(generatedId); // Update the object in memory
                    return generatedId;
                } else {
                    throw new SQLException("Creating document failed, no ID obtained.");
                }
            }}}




    private void upsertFile(Connection conn, FileEntity file, int docId) throws SQLException {

        //move data from StagedFiles to FileEntities
        String moveSql = "INSERT INTO FileEntities (document_id, reference_id, sort_order, rotation, is_barcode, file_data) " +
                "SELECT ?, reference_id, ?, ?, is_barcode, file_data " +
                "FROM StagedFiles WHERE reference_id = ?";

                //Delete staging after moving
               String deleteSql = "DELETE FROM StagedFiles WHERE reference_id = ?;";
        byte[] refBytes = BytesConverter.uuidToBytes(file.getReferenceId());

        try (PreparedStatement movePstmt = conn.prepareStatement(moveSql)) {


            movePstmt.setInt(1, docId);
            movePstmt.setInt(2, file.getSortOrder());
            movePstmt.setInt(3, file.getRotation());
            movePstmt.setBytes(4, refBytes);


            int rowsMoved = movePstmt.executeUpdate();

            if(rowsMoved == 0) {
                try(PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                    deletePstmt.setBytes(1, refBytes);
                    deletePstmt.executeUpdate();
                }
                //if it wasn't in staging
            }else {insertFileNormally(conn, file, docId);}

            }
        }

    private void insertFileNormally(Connection conn, FileEntity file, int docId) throws SQLException {
        String sql = "INSERT INTO FileEntities (document_id, reference_id, sort_order, rotation, is_barcode, file_data) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            pstmt.setBytes(2, BytesConverter.uuidToBytes(file.getReferenceId()));
            pstmt.setInt(3, file.getSortOrder());
            pstmt.setInt(4, file.getRotation());
            pstmt.setBoolean(5, file.isBarcode());
            pstmt.setBytes(6, file.getFileData()); // Sending the heavy bytes now as a backup

            pstmt.executeUpdate();
        }
    }


    /*public void saveBox(Box box) throws DalException {
        String insertBox = "INSERT INTO Boxes (profile_id) VALUES (?)";
        String insertDoc = "INSERT INTO Documents (box_id) VALUES (?)";
        String insertFile = "INSERT INTO FileEntities (document_id, reference_id, sort_order, rotation, is_barcode, file_data) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {

            try {
                conn.setAutoCommit(false); // START TRANSACTION

                try (PreparedStatement boxStmt = conn.prepareStatement(insertBox, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement docStmt = conn.prepareStatement(insertDoc, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement fileStmt = conn.prepareStatement(insertFile)) {

                    // 1. Save Box
                    boxStmt.setString(1, box.getProfileId());
                    boxStmt.executeUpdate();
                    box.setId(getGeneratedId(boxStmt));

                    // 2. Save Documents
                    for (Document doc : box.getDocuments()) {
                        docStmt.setInt(1, box.getId());
                        docStmt.executeUpdate();
                        doc.setId(getGeneratedId(docStmt));

                        // 3. Save Files
                        for (FileEntity file : doc.getFiles()) {
                            fileStmt.setInt(1, doc.getId());
                            fileStmt.setBytes(2, BytesConverter.uuidToBytes(file.getReferenceId()));
                            fileStmt.setInt(3, file.getSortOrder());
                            fileStmt.setInt(4, file.getRotation());
                            fileStmt.setBoolean(5, file.isBarcode());
                            fileStmt.setBytes(6, file.getFileData());
                            fileStmt.addBatch(); // add to the bucket instead of sending immediately
                        }
                        fileStmt.executeBatch(); //send the whole bucket at once
                        conn.commit(); // COMMIT TRANSACTION
                    }


                }
            } catch (SQLException e) {
                conn.rollback(); // Undo everything if it fails
                throw new DalException("Database transaction failed while saving the Box hierarchy.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            // Catch connection issues
            throw new DalException("Could not connect to the database to save the export.", e);
        }
    }*/

    private int getGeneratedId(PreparedStatement stmt) throws DalException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DalException("Failed to retrieve generated ID from database.", e);
        }
        throw new DalException("No ID was generated by the database.");
    }}


