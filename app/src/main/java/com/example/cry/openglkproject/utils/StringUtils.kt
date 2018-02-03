package com.example.cry.openglkproject.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by Cry on 2018/2/3.
 */
fun String.shortToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()