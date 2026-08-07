package com.livewire.rickandmorty

import com.livewire.plugin.logs.LivewireLog
import com.livewire.plugin.network.ktor.LivewireNetworkPlugin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RickAndMortyApi {

  private val httpClient = HttpClient {
    install(ContentNegotiation) {
      json(Json { ignoreUnknownKeys = true })
    }
    install(LivewireNetworkPlugin)
  }

  val imageHttpClient = HttpClient {
    install(LivewireNetworkPlugin)
  }

  suspend fun getCharacters(page: Int = 1): CharacterResponse {
    LivewireLog.d(Tag, "Fetching characters page=$page")
    return try {
      val response = httpClient.get("https://rickandmortyapi.com/api/character?page=$page")
        .body<CharacterResponse>()
      LivewireLog.i(Tag, "Fetched ${response.results.size} characters from page $page")
      response
    } catch (e: Exception) {
      LivewireLog.e(Tag, "Failed to fetch characters page=$page", e)
      throw e
    }
  }

  suspend fun getCharacter(id: Int): Character {
    LivewireLog.d(Tag, "Fetching character id=$id")
    return try {
      val character = httpClient.get("https://rickandmortyapi.com/api/character/$id")
        .body<Character>()
      LivewireLog.i(Tag, "Fetched character ${character.name}")
      character
    } catch (e: Exception) {
      LivewireLog.e(Tag, "Failed to fetch character id=$id", e)
      throw e
    }
  }

  private companion object {
    const val Tag = "RickAndMortyApi"
  }
}
