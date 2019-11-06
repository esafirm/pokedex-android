package id.fathonyfath.pokedex.model

/**
 * Created by fathonyfath on 17/11/17.
 */

data class Pokemon(val id: Int, val name: String, val imageUrl: String, var detail: Detail? = null)

data class Detail(val types: List<String>, val abilities: List<String>, var profile: Profile, var stat: Map<String, Int>)

data class Profile(val weight: Int, val height: Int, val baseExperience: Int)