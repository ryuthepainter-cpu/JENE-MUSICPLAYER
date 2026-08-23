package com.jene.music.core.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.jene.music.data.model.Song

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val duration: Long = 0L,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF
)

class JenePlayerController(context: Context) {
    
    private val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
    private val controllerFuture: ListenableFuture<MediaController> = 
        MediaController.Builder(context, sessionToken).buildAsync()
        
    private var controller: MediaController? = null
    
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    private var currentPlaylist: List<Song> = emptyList()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(PlayerListener())
            updateState()
        }, MoreExecutors.directExecutor())
    }
    
    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        currentPlaylist = songs
        val mediaItems = songs.map { song ->
            val metadataBuilder = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                
            if (song.artworkUri != null) {
                metadataBuilder.setArtworkUri(Uri.parse(song.artworkUri))
            }
            
            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(song.data)
                .setMediaMetadata(metadataBuilder.build())
                .build()
        }
        controller?.setMediaItems(mediaItems, startIndex, 0)
        controller?.prepare()
        controller?.play()
    }
    
    fun playPause() {
        controller?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    fun skipToNext() = controller?.seekToNextMediaItem()
    fun skipToPrevious() = controller?.seekToPreviousMediaItem()
    fun seekTo(positionMs: Long) = controller?.seekTo(positionMs)

    fun setShuffleModeEnabled(enabled: Boolean) {
        controller?.shuffleModeEnabled = enabled
    }

    fun setRepeatMode(repeatMode: Int) {
        controller?.repeatMode = repeatMode
    }
    
    private fun updateState() {
        controller?.let { c ->
            var song = _playerState.value.currentSong
            c.currentMediaItem?.let { mediaItem ->
                val mediaId = mediaItem.mediaId
                song = currentPlaylist.find { it.id == mediaId }
                if (song == null) {
                    val metadata = mediaItem.mediaMetadata
                    song = Song(
                        id = mediaId,
                        data = mediaItem.localConfiguration?.uri?.toString() ?: "",
                        title = metadata.title?.toString() ?: "Unknown",
                        artist = metadata.artist?.toString() ?: "Unknown",
                        album = metadata.albumTitle?.toString() ?: "Unknown",
                        duration = c.duration,
                        artworkUri = metadata.artworkUri?.toString()
                    )
                }
            }

            _playerState.value = _playerState.value.copy(
                currentSong = song,
                isPlaying = c.isPlaying,
                duration = c.duration,
                shuffleModeEnabled = c.shuffleModeEnabled,
                repeatMode = c.repeatMode
            )
            _playbackPosition.value = c.currentPosition

            if (c.isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }
    }

    private fun startProgressTracker() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (true) {
                controller?.let { c ->
                    _playbackPosition.value = c.currentPosition
                }
                delay(100L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
    
    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            updateState()
        }
        
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }
    }
}
