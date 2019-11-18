package id.devfest.pokedex.utils

import androidx.lifecycle.*

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) =
    observe(owner, Observer<T> { v -> observer.invoke(v) })

fun <X> LiveData<X>.asMutable(): MutableLiveData<X> = this as MutableLiveData<X>
