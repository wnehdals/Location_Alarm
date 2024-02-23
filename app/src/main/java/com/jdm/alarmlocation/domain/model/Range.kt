package com.jdm.alarmlocation.domain.model

data class Range(
    override val id: Int,
    override val text: String,
    override var isSelected: Boolean
): ListDialogItem()
