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
        String sql = "INSERT INTO StagedFiles (reference_id,file_data) VALUES (?,?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBytes(1, BytesConverter.uuidToBytes(fileEntity.getReferenceId()));
            pstmt.setBytes(2, fileEntity.getFileData());

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

                    String insertSql = "INSERT INTO FileEntities (document_id,reference_id, sort_order, rotation, is_barcode) VALUES (?,?,?,?,?)";
                    try (PreparedStatement filePstmt = conn.prepareStatement(insertSql)) {
                        for (FileEntity file : doc.getFiles()) {
                            mapFileParams(filePstmt, file, docId);
                            filePstmt.addBatch(); //queue the insert
                        }
                        filePstmt.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
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
            }
        }
    }


    private void upsertFile(Connection conn, FileEntity file, int docId) throws SQLException {
        //if file was inserted after scanning into StagedFiles
        String checkSql = "SELECT 1 FROM StagedFiles WHERE reference_id = ?";
        //insert metadata into FileEntities
        String insertSql = "INSERT INTO FileEntities " +
                "(document_id, reference_id, sort_order, rotation, is_barcode) VALUES (?,?,?,?,?)";
        byte[] refBytes = BytesConverter.uuidToBytes(file.getReferenceId());

        try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
            checkPstmt.setBytes(1, refBytes);

            if (checkPstmt.executeQuery().next()) {
                //File exists in staging, now save the metadata relatinship
                try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                    mapFileParams(insertPstmt, file, docId);
                    insertPstmt.executeUpdate();
                }
            } else
                //not staged ,perform full insert
               try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                   mapFileParams(insertPstmt, file, docId);
                   insertPstmt.executeUpdate();
               }
        } catch (SQLException e) {
            throw new DalException("Upsert failed: " + e.getMessage());
        }
    }
    private void mapFileParams(PreparedStatement pstmt, FileEntity file, int docId) throws SQLException {
        byte[] refBytes = BytesConverter.uuidToBytes(file.getReferenceId());
      pstmt.setInt(1, docId);
      pstmt.setBytes(2, refBytes);
      pstmt.setInt(3, file.getSortOrder());
      pstmt.setInt(4, file.getRotation());
      pstmt.setBoolean(5, file.isBarcode());
    }
}