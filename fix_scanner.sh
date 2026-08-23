#!/bin/bash
cat << 'INNER_EOF' > /tmp/fallback_logic.txt
                    var finalTitle = rawTitle
                    var finalArtist = rawArtist
                    var finalAlbum = rawAlbum
                    var finalGenre = genre
                    
                    if (finalTitle.isNullOrBlank() || finalArtist.isNullOrBlank() || finalArtist == "<unknown>" || finalAlbum.isNullOrBlank() || finalAlbum == "<unknown>") {
                        try {
                            val mmr = android.media.MediaMetadataRetriever()
                            val contentUriTemp = ContentUris.withAppendedId(collection, id)
                            mmr.setDataSource(context, contentUriTemp)
                            
                            if (finalTitle.isNullOrBlank()) {
                                finalTitle = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE)
                            }
                            if (finalArtist.isNullOrBlank() || finalArtist == "<unknown>") {
                                finalArtist = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                            }
                            if (finalAlbum.isNullOrBlank() || finalAlbum == "<unknown>") {
                                finalAlbum = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM)
                            }
                            if (finalGenre.isNullOrBlank()) {
                                finalGenre = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_GENRE)
                            }
                            mmr.release()
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }

                    val title = finalTitle?.takeIf { it.isNotBlank() } ?: fileName.substringBeforeLast(".")
                    val artist = finalArtist?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: albumArtist?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
                    val album = finalAlbum?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: "Unknown Album"
                    val finalGenreOut = finalGenre?.takeIf { it.isNotBlank() } ?: "Unknown Genre"
                    
                    val contentUri = ContentUris.withAppendedId(collection, id).toString()
                    val artworkUri = Uri.parse("content://media/external/audio/albumart/$albumId").toString()
INNER_EOF

# Replace lines from "val title =" to "val artworkUri ="
awk '
/val title = rawTitle/ {
    system("cat /tmp/fallback_logic.txt")
    skip=1
}
/val artworkUri =/ {
    if (skip) {
        skip=0
        next
    }
}
/val finalGenre =/ { if(skip) next }
/val artist =/ { if(skip) next }
/val album =/ { if(skip) next }
/val contentUri =/ { if(skip) next }
{
    if (!skip) print $0
}' app/src/main/java/com/jene/music/data/MediaScanner.kt > /tmp/MediaScanner.kt.new

mv /tmp/MediaScanner.kt.new app/src/main/java/com/jene/music/data/MediaScanner.kt
