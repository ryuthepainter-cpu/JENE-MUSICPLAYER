
import org.jaudiotagger.tag.id3.framebody.FrameBodySYLT
import java.lang.reflect.Modifier

fun main() {
    val methods = FrameBodySYLT::class.java.methods
    for (m in methods) {
        if (Modifier.isPublic(m.modifiers)) {
            println(m.name + " -> " + m.returnType.name)
        }
    }
}
