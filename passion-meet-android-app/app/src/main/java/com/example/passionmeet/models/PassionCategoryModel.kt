package com.example.passionmeet.models

data class PassionCategoryModel(
    val name: String,
    val items: List<PassionSelectorModel>,
    var isExpanded: Boolean = false
)