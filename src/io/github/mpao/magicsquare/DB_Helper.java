/*******************************************************************************
 * This software is distributed under the following BSD license:
 *
 * Copyright (c) 2014, Marco Paoletti <mpao@me.com>, http://mpao.github.io
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package io.github.mpao.magicsquare;

import io.github.mpao.magicsquare.DB_Contract.Classifica;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Helper extends SQLiteOpenHelper {
    // se si cambia lo schema, aumentare il valore di DATABASE_VERSION
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MagicSquare.db";
    public DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Classifica.TABLE_NAME + " (" +
                Classifica._ID + " INTEGER PRIMARY KEY," +
                Classifica.COLUMN_NAME_PUNTEGGIO + " INTEGER," +
                Classifica.COLUMN_NAME_PUNTI + " INTEGER," +
                Classifica.COLUMN_NAME_TEMPO + " INTEGER," +
                Classifica.COLUMN_NAME_HELP + " BOOLEAN," +
                Classifica.COLUMN_NAME_TYPE + " TEXT" +
                " )";
    private static final String SQL_DELETE_ENTRIES = 
            "DROP TABLE IF EXISTS " + Classifica.TABLE_NAME;   
}
