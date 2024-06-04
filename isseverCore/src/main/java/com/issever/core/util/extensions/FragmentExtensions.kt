package com.issever.core.util.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun <T : Any?> Fragment.setBackStackData(key: String, data: T?, doBack: Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (doBack) findNavController().popBackStack()
}

fun <T : Any?> Fragment.getBackStackData(key: String, result: (T?) -> (Unit)) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            result(it)
        }
}