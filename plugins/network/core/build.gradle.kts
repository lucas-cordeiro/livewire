plugins {
  id("livewire.kmp.library")
  id("livewire.publish")
  id("livewire.compose")
  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      api(libs.kotlinx.coroutines.android)
    }
    commonMain.dependencies {
      api(projects.sessions)
      api(projects.ui)
      api(libs.compose.runtime)
      api(libs.kotlinx.coroutines.core)
      api(libs.kotlinx.serialization.json)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
    jvmMain.dependencies {
      api(libs.kotlinx.coroutinesSwing)
    }
  }
}
