package id.devfest.pokedex.data.storage

import id.devfest.pokedex.model.Pokemon
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonStorage @Inject constructor(
    private val pokemonPreference: SimplePokemonPreference
) {

    private val memoryStorage: MutableMap<Int, Pokemon> = mutableMapOf()
    private val subjectMemoryStorage: Subject<MutableMap<Int, Pokemon>> = BehaviorSubject.create()

    init {
        subjectMemoryStorage.onNext(memoryStorage)
    }

    fun clearCache() {
        memoryStorage.clear()
        subjectMemoryStorage.onNext(memoryStorage)
    }

    fun isExist(pokemonId: Int): Boolean {
        val pokemon = memoryStorage[pokemonId]
        return pokemon != null
    }

    fun getPokemon(pokemonId: Int): Pokemon? {
        val inMemory = memoryStorage[pokemonId]
        if (inMemory != null && inMemory.detail == null) {
            val inDisk = pokemonPreference.getPokemon(pokemonId)
            if (inDisk != null) {
                memoryStorage[pokemonId] = inDisk
            }
            return inDisk
        }
        return inMemory
    }

    fun putPokemon(pokemon: Pokemon) {
        pokemonPreference.putPokemon(pokemon)
        updateAndDispatchData(pokemon)
    }

    private fun updateAndDispatchData(pokemon: Pokemon) {
        memoryStorage[pokemon.id] = pokemon
        subjectMemoryStorage.onNext(memoryStorage)
    }

    fun listenToPokemonMap(): Observable<MutableMap<Int, Pokemon>> {
        return subjectMemoryStorage
    }

    fun listenToPokemonId(pokemonId: Int): Observable<Pair<Int, Pokemon>> {
        return listenToPokemonMap()
            .filter { it.containsKey(pokemonId) }
            .map { pokemonId to it[pokemonId]!! }
    }

}