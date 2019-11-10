package id.devfest.pokedex.data.storage

import android.content.Context
import com.google.gson.Gson
import id.devfest.pokedex.model.Pokemon
import javax.inject.Inject

class SimplePokemonPreference @Inject constructor(
    context: Context,
    private val gson: Gson
) {

    companion object {
        private const val KEY_FAVORITE = "FavPokemon"
    }

    private val preferences by lazy {
        context.getSharedPreferences("Pokemon", Context.MODE_PRIVATE)
    }

    fun setFavoritePokemon(pokemon: Pokemon) {
        preferences.edit()
            .putString(KEY_FAVORITE, gson.toJson(pokemon))
            .apply()
    }

    fun getFavoritePokemon(): Pokemon? {
        return getPokemonFromStorage(KEY_FAVORITE)
    }

    fun putPokemon(pokemon: Pokemon) {
        val currentPokemon = getPokemon(pokemon.id)

        // Don't update if the detail is already there
        if (currentPokemon?.detail == null) {
            preferences.edit()
                .putString(pokemon.id.toString(), gson.toJson(pokemon))
                .apply()
        }
    }

    fun getPokemon(id: Int): Pokemon? {
        return getPokemonFromStorage(id.toString())
    }

    private fun getPokemonFromStorage(key: String): Pokemon? {
        return preferences.getString(key, null)?.let {
            gson.fromJson(it, Pokemon::class.java)
        }
    }
}