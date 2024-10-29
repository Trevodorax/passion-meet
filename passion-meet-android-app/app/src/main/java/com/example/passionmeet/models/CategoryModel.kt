package com.example.passionmeet.models

data class CategoryModel(
    val name: String,
    val items: List<PassionSelectorModel>,
    var isExpanded: Boolean = false
)