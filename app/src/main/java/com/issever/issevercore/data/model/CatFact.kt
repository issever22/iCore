package com.issever.issevercore.data.model

data class CatFactsResponse(
    val data: List<CatFact> = listOf()
)

data class CatFact(
    val fact: String = "",
    val length: Int = 0
)