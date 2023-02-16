package com.example.majika.data


data class MenuSection(
    val title:String?=null,
    var type:Int=UIConstant.PARENT,
    var datas: ArrayList<MenuItem> = ArrayList(),
    var isExpanded:Boolean=false
)