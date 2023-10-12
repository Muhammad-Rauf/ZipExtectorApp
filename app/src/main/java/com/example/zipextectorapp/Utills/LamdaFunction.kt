package com.example.zipextectorapp.Utills

import com.example.zipextractor.model.MainEntity

const val TargetPath = "/storage/emulated/0/DCIM/MyZipFile.zip"

var checkBoxSelection: (() -> Unit)? = null
var unSelectAll: (() -> Unit)? = null
var openZipDialog: (() -> Unit)? = null

var checkBoxSelectionZipFragment: (() -> Unit)? = null
var unSelectAllZipFragment: (() -> Unit)? = null
var openZipDialogZipFragment: (() -> Unit)? = null


var sendImageList: ((ArrayList<MainEntity>) -> Unit)? = null






















