package com.example.application2;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "VegeTable";
        public static final String COLUMN_NAME_FOODID = "FoodID";
        public static final String COLUMN_NAME_FOODNAME = "FoodName";
        public static final String COLUMN_NAME_FOODUNITS = "FoodUnits";
    }

    public static class SecondaryTable implements BaseColumns {
        public static final String TABLE_NAME = "PersonTable";
        public static final String COLUMN_NAME_FOODID = "FoodID";
        public static final String COLUMN_NAME_ITEMID = "ItemID";
        public static final String COLUMN_NAME_FOODNAME = "FoodName";
        public static final String COLUMN_NAME_FOODUNITS = "FoodAmount";

    }

    public static class FoodTable implements BaseColumns {
        public static final String TABLE_NAME = "FoodTable";
        public static final String COLUMN_NAME_NDB_NO = "NDB_No";
        public static final String COLUMN_NAME_FDGRP_NO= "FdGrp_No";
        public static final String COLUMN_NAME_Long_Desc = "Long_Desc";
        public static final String COLUMN_NAME_SHORT_DESC = "Short_Desc";
        public static final String COLUMN_NAME_NITROGEN_TO_PROT = "N_Fact";
        public static final String COLUMN_NAME_PROTEIN_FACTOR = "N_Fact";
        public static final String COLUMN_NAME_FAT_FACTOR = "Fat_Fact";
        public static final String COLUMN_NAME_CARB_FACTOR = "Carb_Fact";



    }

    public static class InvTable implements BaseColumns {
        public static final String TABLE_NAME = "InvTable";
        public static final String COLUMN_NAME_INV_ID = "INV_ID";
        public static final String COLUMN_NAME_NDB_NO = "NDB_No";
        public static final String COLUMN_NAME_AMOUNT_NO = "NDB_No";

    }



}
