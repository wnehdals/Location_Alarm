package com.jdm.alarmlocation.domain.model

abstract class ListDialogItem {
    abstract val id: Int
    abstract val text: String
    abstract var isSelected: Boolean
}