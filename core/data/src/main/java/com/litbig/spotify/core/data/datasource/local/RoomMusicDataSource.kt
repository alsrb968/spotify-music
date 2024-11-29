package com.litbig.spotify.core.data.datasource.local

import androidx.paging.PagingSource
import com.litbig.spotify.core.data.db.*
import com.litbig.spotify.core.data.model.local.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RoomMusicDataSource {
    suspend fun insertMetadata(metadata: MusicMetadataEntity)
    suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>)
    fun getAlbums(count: Int = 10): Flow<List<String>>
    fun getPagedAlbums(): PagingSource<Int, String>
    fun getArtists(count: Int = 10): Flow<List<String>>
    fun getPagedArtists(): PagingSource<Int, String>
    fun getArtistFromAlbum(album: String): String
    fun getGenres(count: Int = 10): Flow<List<String>>
    fun getPagedGenres(): PagingSource<Int, String>
    fun getYears(count: Int = 10): Flow<List<String>>
    fun getPagedYears(): PagingSource<Int, String>
    fun getPagedMetadata(): PagingSource<Int, MusicMetadataEntity>
    fun getMetadataByAbsolutePath(absolutePath: String): Flow<MusicMetadataEntity>
    fun getMetadataByAlbum(album: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByAlbum(album: String): PagingSource<Int, MusicMetadataEntity>
    fun getMetadataByArtist(artist: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByArtist(artist: String): PagingSource<Int, MusicMetadataEntity>
    fun getMetadataByGenre(genre: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByGenre(genre: String): PagingSource<Int, MusicMetadataEntity>
    fun getMetadataByYear(year: String): Flow<MusicMetadataEntity>
    fun getPagedMetadataByYear(year: String): PagingSource<Int, MusicMetadataEntity>
    suspend fun isExistMetadata(absolutePath: String): Boolean
    suspend fun deleteAllMetadataList()
    suspend fun deleteMetadata(absolutePath: String)
    suspend fun getMetadataCount(): Int
    suspend fun getMetadataCountByAlbum(album: String): Int
    suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int
    suspend fun getMetadataCountByArtist(artist: String): Int
    suspend fun getMetadataCountByGenre(genre: String): Int
    suspend fun getMetadataCountByYear(year: String): Int

    suspend fun insertAlbumArt(albumArt: AlbumArtEntity)
    suspend fun getAlbumArtByAlbum(album: String): AlbumArtEntity?
    suspend fun getAlbumArtById(id: String): AlbumArtEntity?
    suspend fun deleteAllAlbumArt()

    suspend fun insertArtistInfo(artistInfo: ArtistInfoEntity)
    suspend fun getArtistInfoByArtist(artist: String): ArtistInfoEntity?
    suspend fun getArtistInfoById(id: String): ArtistInfoEntity?
    suspend fun deleteAllArtistInfo()

    suspend fun insertFavorite(favorite: FavoriteEntity)
    fun isFavorite(name: String, type: String): Flow<Boolean>
    fun getFavorites(count: Int = 10): Flow<List<FavoriteEntity>>
    fun getPagedFavorites(): PagingSource<Int, FavoriteEntity>
    fun getPagedFavoritesByType(type: String): PagingSource<Int, FavoriteEntity>
    suspend fun deleteFavorite(name: String, type: String)
    suspend fun deleteAllFavorites()

    suspend fun insertStorageHash(storageHash: StorageHashEntity)
    suspend fun getHash(path: String): String?
    suspend fun deleteStorageHash(path: String)
    suspend fun deleteAllStorageHash()
}

class RoomMusicDataSourceImpl @Inject constructor(
    private val metadataDao: MusicMetadataDao,
    private val albumArtDao: AlbumArtDao,
    private val artistInfoDao: ArtistInfoDao,
    private val favoriteDao: FavoriteDao,
    private val storageHashDao: StorageHashDao,
) : RoomMusicDataSource {
    override suspend fun insertMetadata(metadata: MusicMetadataEntity) {
        metadataDao.insertMetadata(metadata)
    }

    override suspend fun insertMetadataList(metadataList: List<MusicMetadataEntity>) {
        metadataDao.insertMetadataList(metadataList)
    }

    override fun getAlbums(count: Int): Flow<List<String>> {
        return metadataDao.getAlbums(count)
    }

    override fun getPagedAlbums(): PagingSource<Int, String> {
        return metadataDao.getPagedAlbums()
    }

    override fun getArtists(count: Int): Flow<List<String>> {
        return metadataDao.getArtists(count)
    }

    override fun getPagedArtists(): PagingSource<Int, String> {
        return metadataDao.getPagedArtists()
    }

    override fun getArtistFromAlbum(album: String): String {
        return metadataDao.getArtistFromAlbum(album)
    }

    override fun getGenres(count: Int): Flow<List<String>> {
        return metadataDao.getGenres(count)
    }

    override fun getPagedGenres(): PagingSource<Int, String> {
        return metadataDao.getPagedGenres()
    }

    override fun getYears(count: Int): Flow<List<String>> {
        return metadataDao.getYears(count)
    }

    override fun getPagedYears(): PagingSource<Int, String> {
        return metadataDao.getPagedYears()
    }

    override fun getPagedMetadata(): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadata()
    }

    override fun getMetadataByAbsolutePath(absolutePath: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByAbsolutePath(absolutePath)
    }

    override fun getMetadataByAlbum(album: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByAlbum(album)
    }

    override fun getPagedMetadataByAlbum(album: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByAlbum(album)
    }

    override fun getMetadataByArtist(artist: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByArtist(artist)
    }

    override fun getPagedMetadataByArtist(artist: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByArtist(artist)
    }

    override fun getMetadataByGenre(genre: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByGenre(genre)
    }

    override fun getPagedMetadataByGenre(genre: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByGenre(genre)
    }

    override fun getMetadataByYear(year: String): Flow<MusicMetadataEntity> {
        return metadataDao.getMetadataByYear(year)
    }

    override fun getPagedMetadataByYear(year: String): PagingSource<Int, MusicMetadataEntity> {
        return metadataDao.getPagedMetadataByYear(year)
    }

    override suspend fun isExistMetadata(absolutePath: String): Boolean {
        return metadataDao.isExistMetadata(absolutePath)
    }

    override suspend fun deleteAllMetadataList() {
        metadataDao.deleteAllMetadataList()
    }

    override suspend fun deleteMetadata(absolutePath: String) {
        metadataDao.deleteMetadata(absolutePath)
    }

    override suspend fun getMetadataCount(): Int {
        return metadataDao.getMetadataCount()
    }

    override suspend fun getMetadataCountByAlbum(album: String): Int {
        return metadataDao.getMetadataCountByAlbum(album)
    }

    override suspend fun getMetadataCountByAlbumOfArtist(artist: String): Int {
        return metadataDao.getMetadataCountByAlbumOfArtist(artist)
    }

    override suspend fun getMetadataCountByArtist(artist: String): Int {
        return metadataDao.getMetadataCountByArtist(artist)
    }

    override suspend fun getMetadataCountByGenre(genre: String): Int {
        return metadataDao.getMetadataCountByGenre(genre)
    }

    override suspend fun getMetadataCountByYear(year: String): Int {
        return metadataDao.getMetadataCountByYear(year)
    }

    override suspend fun insertAlbumArt(albumArt: AlbumArtEntity) {
        albumArtDao.insertAlbumArt(albumArt)
    }

    override suspend fun getAlbumArtByAlbum(album: String): AlbumArtEntity? {
        return albumArtDao.getAlbumArtByAlbum(album)
    }

    override suspend fun getAlbumArtById(id: String): AlbumArtEntity? {
        return albumArtDao.getAlbumArtById(id)
    }

    override suspend fun deleteAllAlbumArt() {
        albumArtDao.deleteAllAlbumArt()
    }

    override suspend fun insertArtistInfo(artistInfo: ArtistInfoEntity) {
        artistInfoDao.insertArtistInfo(artistInfo)
    }

    override suspend fun getArtistInfoByArtist(artist: String): ArtistInfoEntity? {
        return artistInfoDao.getArtistInfoByArtist(artist)
    }

    override suspend fun getArtistInfoById(id: String): ArtistInfoEntity? {
        return artistInfoDao.getArtistInfoById(id)
    }

    override suspend fun deleteAllArtistInfo() {
        artistInfoDao.deleteAllArtistInfo()
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    override fun isFavorite(name: String, type: String): Flow<Boolean> {
        return favoriteDao.isFavorite(name, type)
    }

    override fun getFavorites(count: Int): Flow<List<FavoriteEntity>> {
        return favoriteDao.getFavorites(count)
    }

    override fun getPagedFavorites(): PagingSource<Int, FavoriteEntity> {
        return favoriteDao.getPagedFavorites()
    }

    override fun getPagedFavoritesByType(type: String): PagingSource<Int, FavoriteEntity> {
        return favoriteDao.getPagedFavoritesByType(type)
    }

    override suspend fun deleteFavorite(name: String, type: String) {
        favoriteDao.deleteFavorite(name, type)
    }

    override suspend fun deleteAllFavorites() {
        favoriteDao.deleteAllFavorites()
    }

    override suspend fun insertStorageHash(storageHash: StorageHashEntity) {
        storageHashDao.insertStorageHash(storageHash)
    }

    override suspend fun getHash(path: String): String? {
        return storageHashDao.getHash(path)
    }

    override suspend fun deleteStorageHash(path: String) {
        storageHashDao.deleteStorageHash(path)
    }

    override suspend fun deleteAllStorageHash() {
        storageHashDao.deleteAllStorageHash()
    }
}