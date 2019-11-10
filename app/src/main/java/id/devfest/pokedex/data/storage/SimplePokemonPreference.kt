package id.devfest.pokedex.data.storage

import android.content.Context
import com.google.gson.Gson
import id.devfest.pokedex.model.Pokemon
import javax.inject.Inject

class SimplePokemonPreference @Inject constructor(
    context: Context,
    private val gson: Gson
) {

    private val preferences by lazy {
        context.getSharedPreferences("Pokemon", Context.MODE_PRIVATE)
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
        return preferences.getString(id.toString(), null)?.let {
            gson.fromJson(it, Pokemon::class.java)
        }
    }
}