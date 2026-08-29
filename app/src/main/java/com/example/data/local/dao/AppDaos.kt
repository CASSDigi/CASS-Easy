package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BusinessCardEntity
import com.example.data.local.entity.CustomQrEntity
import com.example.data.local.entity.ScanRecordEntity
import com.example.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteScans(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_history WHERE contentType = :contentType ORDER BY timestamp DESC")
    fun getScansByType(contentType: String): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_history WHERE barcodeFormat = :barcodeFormat ORDER BY timestamp DESC")
    fun getScansByBarcodeFormat(barcodeFormat: String): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_history WHERE title LIKE '%' || :query || '%' OR rawText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_history WHERE id = :id LIMIT 1")
    fun getScanById(id: Long): Flow<ScanRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanRecordEntity): Long

    @Update
    suspend fun updateScan(scan: ScanRecordEntity)

    @Delete
    suspend fun deleteScan(scan: ScanRecordEntity)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteScanById(id: Long)

    @Query("DELETE FROM scan_history")
    suspend fun clearAllScans()

    @Query("SELECT COUNT(*) FROM scan_history")
    fun getScanCount(): Flow<Int>
}

@Dao
interface CustomQrDao {
    @Query("SELECT * FROM custom_qr_codes ORDER BY timestamp DESC")
    fun getAllCustomQrs(): Flow<List<CustomQrEntity>>

    @Query("SELECT * FROM custom_qr_codes WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteQrs(): Flow<List<CustomQrEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomQr(qr: CustomQrEntity): Long

    @Update
    suspend fun updateCustomQr(qr: CustomQrEntity)

    @Delete
    suspend fun deleteCustomQr(qr: CustomQrEntity)

    @Query("DELETE FROM custom_qr_codes WHERE id = :id")
    suspend fun deleteCustomQrById(id: Long)
}

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY timestamp DESC")
    fun getAllVaultItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE category = :category ORDER BY timestamp DESC")
    fun getVaultItemsByCategory(category: String): Flow<List<VaultItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultItemEntity): Long

    @Update
    suspend fun updateVaultItem(item: VaultItemEntity)

    @Delete
    suspend fun deleteVaultItem(item: VaultItemEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteVaultItemById(id: Long)
}

@Dao
interface BusinessCardDao {
    @Query("SELECT * FROM business_cards ORDER BY isPrimary DESC, timestamp DESC")
    fun getAllCards(): Flow<List<BusinessCardEntity>>

    @Query("SELECT * FROM business_cards WHERE isPrimary = 1 LIMIT 1")
    fun getPrimaryCard(): Flow<BusinessCardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BusinessCardEntity): Long

    @Update
    suspend fun updateCard(card: BusinessCardEntity)

    @Delete
    suspend fun deleteCard(card: BusinessCardEntity)
}
