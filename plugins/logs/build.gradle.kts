plugins {
  id("livewire.kmp.library")
  id("livewire.publish")
  id("livewire.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.ui)
      api(libs.compose.runtime)
      api(libs.kotlinx.coroutines.core)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
  }
}
