plugins {
  id("livewire.kmp.library")
  id("livewire.publish")
  id("livewire.compose")
  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
    }
    commonMain.dependencies {
      api(projects.ui)
      api(libs.kotlinx.coroutines.core)
      api(libs.kotlinx.serialization.json)
      api(libs.okio)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
  }
}
