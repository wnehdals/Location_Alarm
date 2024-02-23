package com.jdm.alarmlocation.domain.model

data class Way(
    override val id: Int,
    override val text: String,
    override var isSelected: Boolean
) : ListDialogItem()
