plugins {
    buildsrc.convention.`kotlin-jvm`
}

dependencies {
    api(libs.gdx)
    api(libs.gdxTools)
}

tasks.register<JavaExec>("packTextures") {
    group = "tools"
    description = "Runs TexturePacker on the assets_raw directory to generate the atlas files"

    mainClass.set("io.github.quillraven.descent.TexturePackerKt")
    classpath = sourceSets.main.get().runtimeClasspath
}
