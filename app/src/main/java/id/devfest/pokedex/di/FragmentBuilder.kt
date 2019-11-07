package id.devfest.pokedex.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.devfest.pokedex.DetailDialog

/**
 * Created by fathonyfath on 21/03/18.
 */

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract fun bindDetailDialog(): DetailDialog
}