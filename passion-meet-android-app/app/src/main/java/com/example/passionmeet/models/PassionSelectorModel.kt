package com.example.passionmeet.models

data class PassionSelectorModel(
    val name: String,
    val image: String,
    var isSelected: Boolean = false
) {
}