package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "POTENTIAL_CUSTOMERS_ENTITY".
*/
public class PotentialCustomersEntityDao extends AbstractDao<PotentialCustomersEntity, String> {

    public static final String TABLENAME = "POTENTIAL_CUSTOMERS_ENTITY";

    /**
     * Properties of entity PotentialCustomersEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property _id = new Property(0, String.class, "_id", true, "_ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Phone = new Property(2, String.class, "phone", false, "PHONE");
        public final static Property NameInitial = new Property(3, String.class, "nameInitial", false, "NAME_INITIAL");
        public final static Property NamePinyin = new Property(4, String.class, "namePinyin", false, "NAME_PINYIN");
        public final static Property IsRegistered = new Property(5, boolean.class, "isRegistered", false, "IS_REGISTERED");
        public final static Property Sex = new Property(6, boolean.class, "sex", false, "SEX");
        public final static Property NameInitialType = new Property(7, int.class, "nameInitialType", false, "NAME_INITIAL_TYPE");
    };


    public PotentialCustomersEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PotentialCustomersEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"POTENTIAL_CUSTOMERS_ENTITY\" (" + //
                "\"_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: _id
                "\"NAME\" TEXT," + // 1: name
                "\"PHONE\" TEXT," + // 2: phone
                "\"NAME_INITIAL\" TEXT," + // 3: nameInitial
                "\"NAME_PINYIN\" TEXT," + // 4: namePinyin
                "\"IS_REGISTERED\" INTEGER NOT NULL ," + // 5: isRegistered
                "\"SEX\" INTEGER NOT NULL ," + // 6: sex
                "\"NAME_INITIAL_TYPE\" INTEGER NOT NULL );"); // 7: nameInitialType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"POTENTIAL_CUSTOMERS_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PotentialCustomersEntity entity) {
        stmt.clearBindings();
 
        String _id = entity.get_id();
        if (_id != null) {
            stmt.bindString(1, _id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(3, phone);
        }
 
        String nameInitial = entity.getNameInitial();
        if (nameInitial != null) {
            stmt.bindString(4, nameInitial);
        }
 
        String namePinyin = entity.getNamePinyin();
        if (namePinyin != null) {
            stmt.bindString(5, namePinyin);
        }
        stmt.bindLong(6, entity.getIsRegistered() ? 1L: 0L);
        stmt.bindLong(7, entity.getSex() ? 1L: 0L);
        stmt.bindLong(8, entity.getNameInitialType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PotentialCustomersEntity entity) {
        stmt.clearBindings();
 
        String _id = entity.get_id();
        if (_id != null) {
            stmt.bindString(1, _id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(3, phone);
        }
 
        String nameInitial = entity.getNameInitial();
        if (nameInitial != null) {
            stmt.bindString(4, nameInitial);
        }
 
        String namePinyin = entity.getNamePinyin();
        if (namePinyin != null) {
            stmt.bindString(5, namePinyin);
        }
        stmt.bindLong(6, entity.getIsRegistered() ? 1L: 0L);
        stmt.bindLong(7, entity.getSex() ? 1L: 0L);
        stmt.bindLong(8, entity.getNameInitialType());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public PotentialCustomersEntity readEntity(Cursor cursor, int offset) {
        PotentialCustomersEntity entity = new PotentialCustomersEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // phone
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // nameInitial
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // namePinyin
            cursor.getShort(offset + 5) != 0, // isRegistered
            cursor.getShort(offset + 6) != 0, // sex
            cursor.getInt(offset + 7) // nameInitialType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PotentialCustomersEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPhone(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNameInitial(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNamePinyin(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsRegistered(cursor.getShort(offset + 5) != 0);
        entity.setSex(cursor.getShort(offset + 6) != 0);
        entity.setNameInitialType(cursor.getInt(offset + 7));
     }
    
    @Override
    protected final String updateKeyAfterInsert(PotentialCustomersEntity entity, long rowId) {
        return entity.get_id();
    }
    
    @Override
    public String getKey(PotentialCustomersEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
