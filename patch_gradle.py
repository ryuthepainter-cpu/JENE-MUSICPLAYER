import re

with open("gradle/libs.versions.toml", "r") as f:
    toml = f.read()

toml = toml.replace(
    'googleid = "1.1.1"\n',
    'googleid = "1.1.1"\njaudiotagger = "3.0.1"\n'
)

toml = toml.replace(
    'googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }\n',
    'googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }\njaudiotagger = { group = "net.jthink", name = "jaudiotagger", version.ref = "jaudiotagger" }\n'
)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(toml)

with open("app/build.gradle.kts", "r") as f:
    gradle = f.read()

gradle = gradle.replace(
    'implementation(libs.androidx.media3.common)',
    'implementation(libs.androidx.media3.common)\n  implementation(libs.jaudiotagger)'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(gradle)
