sed -i 's/}.sortedBy { it.name }/}.sortedBy { it.name }\n    }.flowOn(Dispatchers.Default)/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i '1s/^/import kotlinx.coroutines.Dispatchers\n/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
