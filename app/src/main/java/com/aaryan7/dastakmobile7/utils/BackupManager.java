package com.aaryan7.dastakmobile7.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Utility class for Google Drive backup and restore
 */
public class BackupManager {
    private static final String TAG = "BackupManager";
    private static final String PREF_NAME = "backup_prefs";
    private static final String KEY_LAST_BACKUP = "last_backup";
    private static final String BACKUP_FOLDER_NAME = "DastakMobile7Backup";
    private static final String DATABASE_BACKUP_NAME = "dastak_mobile_7.db";
    
    private Context context;
    private SharedPreferences preferences;
    private DriveResourceClient driveResourceClient;
    private GoogleSignInClient googleSignInClient;
    
    public BackupManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // Configure sign-in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, signInOptions);
    }
    
    /**
     * Check if user is signed in to Google
     * @return true if signed in, false otherwise
     */
    public boolean isUserSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null;
    }
    
    /**
     * Get Google Sign-In client
     * @return GoogleSignInClient
     */
    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }
    
    /**
     * Initialize Drive client
     * @param account Google account
     */
    public void initializeDriveClient(GoogleSignInAccount account) {
        driveResourceClient = Drive.getDriveResourceClient(context, account);
    }
    
    /**
     * Create backup folder if it doesn't exist
     * @return Task with backup folder
     */
    private Task<DriveFolder> getOrCreateBackupFolder() {
        // Query for existing folder
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, BACKUP_FOLDER_NAME))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                .build();
        
        return driveResourceClient.query(query)
                .continueWithTask(task -> {
                    MetadataBuffer metadataBuffer = task.getResult();
                    
                    if (metadataBuffer != null && metadataBuffer.getCount() > 0) {
                        // Folder exists, return it
                        DriveFolder folder = metadataBuffer.get(0).getDriveId().asDriveFolder();
                        metadataBuffer.release();
                        return Tasks.forResult(folder);
                    } else {
                        // Folder doesn't exist, create it
                        if (metadataBuffer != null) {
                            metadataBuffer.release();
                        }
                        
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(BACKUP_FOLDER_NAME)
                                .setMimeType(DriveFolder.MIME_TYPE)
                                .build();
                        
                        return driveResourceClient.getRootFolder()
                                .continueWithTask(rootTask -> 
                                        driveResourceClient.createFolder(rootTask.getResult(), changeSet));
                    }
                });
    }
    
    /**
     * Backup database to Google Drive
     * @return Task with backup result
     */
    public Task<Void> backupDatabase() {
        if (driveResourceClient == null) {
            return Tasks.forException(new IllegalStateException("Drive client not initialized"));
        }
        
        // Get database file
        File dbFile = context.getDatabasePath("dastak_mobile_7.db");
        
        if (!dbFile.exists()) {
            return Tasks.forException(new IllegalStateException("Database file not found"));
        }
        
        return getOrCreateBackupFolder()
                .continueWithTask(folderTask -> {
                    DriveFolder backupFolder = folderTask.getResult();
                    
                    // Check for existing backup file
                    Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, DATABASE_BACKUP_NAME))
                            .build();
                    
                    return driveResourceClient.query(backupFolder, query)
                            .continueWithTask(queryTask -> {
                                MetadataBuffer metadataBuffer = queryTask.getResult();
                                
                                if (metadataBuffer != null && metadataBuffer.getCount() > 0) {
                                    // File exists, update it
                                    DriveFile file = metadataBuffer.get(0).getDriveId().asDriveFile();
                                    metadataBuffer.release();
                                    return updateBackupFile(file, dbFile);
                                } else {
                                    // File doesn't exist, create it
                                    if (metadataBuffer != null) {
                                        metadataBuffer.release();
                                    }
                                    return createBackupFile(backupFolder, dbFile);
                                }
                            });
                })
                .continueWithTask(task -> {
                    // Update last backup time
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(KEY_LAST_BACKUP, System.currentTimeMillis());
                    editor.apply();
                    
                    return Tasks.forResult(null);
                });
    }
    
    /**
     * Create new backup file
     * @param folder Backup folder
     * @param dbFile Database file
     * @return Task with result
     */
    private Task<DriveFile> createBackupFile(DriveFolder folder, File dbFile) {
        return driveResourceClient.createContents()
                .continueWithTask(contentsTask -> {
                    DriveContents contents = contentsTask.getResult();
                    
                    // Write database to contents
                    try (OutputStream outputStream = contents.getOutputStream();
                         FileInputStream inputStream = new FileInputStream(dbFile)) {
                        
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error writing database to contents", e);
                        return Tasks.forException(e);
                    }
                    
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(DATABASE_BACKUP_NAME)
                            .setMimeType("application/octet-stream")
                            .build();
                    
                    return driveResourceClient.createFile(folder, changeSet, contents);
                });
    }
    
    /**
     * Update existing backup file
     * @param file Backup file
     * @param dbFile Database file
     * @return Task with result
     */
    private Task<Void> updateBackupFile(DriveFile file, File dbFile) {
        return driveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY)
                .continueWithTask(contentsTask -> {
                    DriveContents contents = contentsTask.getResult();
                    
                    // Write database to contents
                    try (OutputStream outputStream = contents.getOutputStream();
                         FileInputStream inputStream = new FileInputStream(dbFile)) {
                        
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error writing database to contents", e);
                        return Tasks.forException(e);
                    }
                    
                    return driveResourceClient.commitContents(contents, null);
                });
    }
    
    /**
     * Restore database from Google Drive
     * @return Task with restore result
     */
    public Task<Void> restoreDatabase() {
        if (driveResourceClient == null) {
            return Tasks.forException(new IllegalStateException("Drive client not initialized"));
        }
        
        return getOrCreateBackupFolder()
                .continueWithTask(folderTask -> {
                    DriveFolder backupFolder = folderTask.getResult();
                    
                    // Check for existing backup file
                    Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, DATABASE_BACKUP_NAME))
                            .build();
                    
                    return driveResourceClient.query(backupFolder, query);
                })
                .continueWithTask(queryTask -> {
                    MetadataBuffer metadataBuffer = queryTask.getResult();
                    
                    if (metadataBuffer != null && metadataBuffer.getCount() > 0) {
                        // File exists, download it
                        DriveFile file = metadataBuffer.get(0).getDriveId().asDriveFile();
                        metadataBuffer.release();
                        return downloadBackupFile(file);
                    } else {
                        // File doesn't exist
                        if (metadataBuffer != null) {
                            metadataBuffer.release();
                        }
                        return Tasks.forException(new IllegalStateException("No backup file found"));
                    }
                });
    }
    
    /**
     * Download backup file and restore database
     * @param file Backup file
     * @return Task with result
     */
    private Task<Void> downloadBackupFile(DriveFile file) {
        return driveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY)
                .continueWithTask(contentsTask -> {
                    DriveContents contents = contentsTask.getResult();
                    
                    // Get database file
                    File dbFile = context.getDatabasePath("dastak_mobile_7.db");
                    
                    // Ensure parent directory exists
                    if (!dbFile.getParentFile().exists()) {
                        dbFile.getParentFile().mkdirs();
                    }
                    
                    // Write contents to database file
                    try (InputStream inputStream = contents.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(dbFile)) {
                        
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error writing contents to database", e);
                        return Tasks.forException(e);
                    }
                    
                    return driveResourceClient.discardContents(contents);
                });
    }
    
    /**
     * Get last backup time
     * @return Last backup time in milliseconds, 0 if never backed up
     */
    public long getLastBackupTime() {
        return preferences.getLong(KEY_LAST_BACKUP, 0);
    }
    
    /**
     * Check if automatic backup is needed (more than 24 hours since last backup)
     * @return true if backup is needed, false otherwise
     */
    public boolean isBackupNeeded() {
        long lastBackup = getLastBackupTime();
        long currentTime = System.currentTimeMillis();
        
        // If never backed up, backup is needed
        if (lastBackup == 0) {
            return true;
        }
        
        // If more than 24 hours since last backup, backup is needed
        long hoursSinceLastBackup = (currentTime - lastBackup) / (1000 * 60 * 60);
        return hoursSinceLastBackup >= 24;
    }
}
