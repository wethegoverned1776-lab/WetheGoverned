package net.wetheGoverned.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import net.wetheGoverned.local.entity.ResidentProfileEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ResidentProfileDao_Impl implements ResidentProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ResidentProfileEntity> __insertionAdapterOfResidentProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTier;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTierWithFingerprint;

  public ResidentProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfResidentProfileEntity = new EntityInsertionAdapter<ResidentProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `resident_profiles` (`pubKey`,`displayName`,`federalHouseId`,`federalSenateId`,`stateSenateId`,`stateHouseId`,`countyId`,`cityId`,`schoolBoardId`,`tier`,`avatarUrl`,`joinedAt`,`addressFingerprint`,`verifiedByPubKey`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ResidentProfileEntity entity) {
        statement.bindString(1, entity.getPubKey());
        statement.bindString(2, entity.getDisplayName());
        if (entity.getFederalHouseId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFederalHouseId());
        }
        if (entity.getFederalSenateId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFederalSenateId());
        }
        if (entity.getStateSenateId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStateSenateId());
        }
        if (entity.getStateHouseId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getStateHouseId());
        }
        if (entity.getCountyId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCountyId());
        }
        if (entity.getCityId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCityId());
        }
        if (entity.getSchoolBoardId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSchoolBoardId());
        }
        statement.bindString(10, entity.getTier());
        if (entity.getAvatarUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getAvatarUrl());
        }
        statement.bindLong(12, entity.getJoinedAt());
        if (entity.getAddressFingerprint() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getAddressFingerprint());
        }
        if (entity.getVerifiedByPubKey() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getVerifiedByPubKey());
        }
        statement.bindLong(15, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfUpdateTier = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE resident_profiles SET tier = ? WHERE pubKey = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTierWithFingerprint = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE resident_profiles SET tier = ?, addressFingerprint = ? WHERE pubKey = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertProfile(final ResidentProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfResidentProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTier(final String pubKey, final String tier,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTier.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, tier);
        _argIndex = 2;
        _stmt.bindString(_argIndex, pubKey);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTier.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTierWithFingerprint(final String pubKey, final String tier,
      final String fingerprint, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTierWithFingerprint.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, tier);
        _argIndex = 2;
        _stmt.bindString(_argIndex, fingerprint);
        _argIndex = 3;
        _stmt.bindString(_argIndex, pubKey);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTierWithFingerprint.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ResidentProfileEntity> observeProfile(final String pubKey) {
    final String _sql = "SELECT * FROM resident_profiles WHERE pubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pubKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"resident_profiles"}, new Callable<ResidentProfileEntity>() {
      @Override
      @Nullable
      public ResidentProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFederalHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalHouseId");
          final int _cursorIndexOfFederalSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalSenateId");
          final int _cursorIndexOfStateSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateSenateId");
          final int _cursorIndexOfStateHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateHouseId");
          final int _cursorIndexOfCountyId = CursorUtil.getColumnIndexOrThrow(_cursor, "countyId");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfSchoolBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolBoardId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFederalHouseId;
            if (_cursor.isNull(_cursorIndexOfFederalHouseId)) {
              _tmpFederalHouseId = null;
            } else {
              _tmpFederalHouseId = _cursor.getString(_cursorIndexOfFederalHouseId);
            }
            final String _tmpFederalSenateId;
            if (_cursor.isNull(_cursorIndexOfFederalSenateId)) {
              _tmpFederalSenateId = null;
            } else {
              _tmpFederalSenateId = _cursor.getString(_cursorIndexOfFederalSenateId);
            }
            final String _tmpStateSenateId;
            if (_cursor.isNull(_cursorIndexOfStateSenateId)) {
              _tmpStateSenateId = null;
            } else {
              _tmpStateSenateId = _cursor.getString(_cursorIndexOfStateSenateId);
            }
            final String _tmpStateHouseId;
            if (_cursor.isNull(_cursorIndexOfStateHouseId)) {
              _tmpStateHouseId = null;
            } else {
              _tmpStateHouseId = _cursor.getString(_cursorIndexOfStateHouseId);
            }
            final String _tmpCountyId;
            if (_cursor.isNull(_cursorIndexOfCountyId)) {
              _tmpCountyId = null;
            } else {
              _tmpCountyId = _cursor.getString(_cursorIndexOfCountyId);
            }
            final String _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getString(_cursorIndexOfCityId);
            }
            final String _tmpSchoolBoardId;
            if (_cursor.isNull(_cursorIndexOfSchoolBoardId)) {
              _tmpSchoolBoardId = null;
            } else {
              _tmpSchoolBoardId = _cursor.getString(_cursorIndexOfSchoolBoardId);
            }
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFederalHouseId,_tmpFederalSenateId,_tmpStateSenateId,_tmpStateHouseId,_tmpCountyId,_tmpCityId,_tmpSchoolBoardId,_tmpTier,_tmpAvatarUrl,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ResidentProfileEntity>> observeProfilesByFingerprint(final String fingerprint) {
    final String _sql = "SELECT * FROM resident_profiles WHERE addressFingerprint = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fingerprint);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"resident_profiles"}, new Callable<List<ResidentProfileEntity>>() {
      @Override
      @NonNull
      public List<ResidentProfileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFederalHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalHouseId");
          final int _cursorIndexOfFederalSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalSenateId");
          final int _cursorIndexOfStateSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateSenateId");
          final int _cursorIndexOfStateHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateHouseId");
          final int _cursorIndexOfCountyId = CursorUtil.getColumnIndexOrThrow(_cursor, "countyId");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfSchoolBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolBoardId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<ResidentProfileEntity> _result = new ArrayList<ResidentProfileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ResidentProfileEntity _item;
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFederalHouseId;
            if (_cursor.isNull(_cursorIndexOfFederalHouseId)) {
              _tmpFederalHouseId = null;
            } else {
              _tmpFederalHouseId = _cursor.getString(_cursorIndexOfFederalHouseId);
            }
            final String _tmpFederalSenateId;
            if (_cursor.isNull(_cursorIndexOfFederalSenateId)) {
              _tmpFederalSenateId = null;
            } else {
              _tmpFederalSenateId = _cursor.getString(_cursorIndexOfFederalSenateId);
            }
            final String _tmpStateSenateId;
            if (_cursor.isNull(_cursorIndexOfStateSenateId)) {
              _tmpStateSenateId = null;
            } else {
              _tmpStateSenateId = _cursor.getString(_cursorIndexOfStateSenateId);
            }
            final String _tmpStateHouseId;
            if (_cursor.isNull(_cursorIndexOfStateHouseId)) {
              _tmpStateHouseId = null;
            } else {
              _tmpStateHouseId = _cursor.getString(_cursorIndexOfStateHouseId);
            }
            final String _tmpCountyId;
            if (_cursor.isNull(_cursorIndexOfCountyId)) {
              _tmpCountyId = null;
            } else {
              _tmpCountyId = _cursor.getString(_cursorIndexOfCountyId);
            }
            final String _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getString(_cursorIndexOfCityId);
            }
            final String _tmpSchoolBoardId;
            if (_cursor.isNull(_cursorIndexOfSchoolBoardId)) {
              _tmpSchoolBoardId = null;
            } else {
              _tmpSchoolBoardId = _cursor.getString(_cursorIndexOfSchoolBoardId);
            }
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFederalHouseId,_tmpFederalSenateId,_tmpStateSenateId,_tmpStateHouseId,_tmpCountyId,_tmpCityId,_tmpSchoolBoardId,_tmpTier,_tmpAvatarUrl,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProfileCountByFingerprint(final String fingerprint,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM resident_profiles WHERE addressFingerprint = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fingerprint);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getVouchCount(final String notaryPubKey,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM resident_profiles WHERE verifiedByPubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, notaryPubKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllProfiles(
      final Continuation<? super List<ResidentProfileEntity>> $completion) {
    final String _sql = "SELECT * FROM resident_profiles";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ResidentProfileEntity>>() {
      @Override
      @NonNull
      public List<ResidentProfileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFederalHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalHouseId");
          final int _cursorIndexOfFederalSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalSenateId");
          final int _cursorIndexOfStateSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateSenateId");
          final int _cursorIndexOfStateHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateHouseId");
          final int _cursorIndexOfCountyId = CursorUtil.getColumnIndexOrThrow(_cursor, "countyId");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfSchoolBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolBoardId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<ResidentProfileEntity> _result = new ArrayList<ResidentProfileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ResidentProfileEntity _item;
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFederalHouseId;
            if (_cursor.isNull(_cursorIndexOfFederalHouseId)) {
              _tmpFederalHouseId = null;
            } else {
              _tmpFederalHouseId = _cursor.getString(_cursorIndexOfFederalHouseId);
            }
            final String _tmpFederalSenateId;
            if (_cursor.isNull(_cursorIndexOfFederalSenateId)) {
              _tmpFederalSenateId = null;
            } else {
              _tmpFederalSenateId = _cursor.getString(_cursorIndexOfFederalSenateId);
            }
            final String _tmpStateSenateId;
            if (_cursor.isNull(_cursorIndexOfStateSenateId)) {
              _tmpStateSenateId = null;
            } else {
              _tmpStateSenateId = _cursor.getString(_cursorIndexOfStateSenateId);
            }
            final String _tmpStateHouseId;
            if (_cursor.isNull(_cursorIndexOfStateHouseId)) {
              _tmpStateHouseId = null;
            } else {
              _tmpStateHouseId = _cursor.getString(_cursorIndexOfStateHouseId);
            }
            final String _tmpCountyId;
            if (_cursor.isNull(_cursorIndexOfCountyId)) {
              _tmpCountyId = null;
            } else {
              _tmpCountyId = _cursor.getString(_cursorIndexOfCountyId);
            }
            final String _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getString(_cursorIndexOfCityId);
            }
            final String _tmpSchoolBoardId;
            if (_cursor.isNull(_cursorIndexOfSchoolBoardId)) {
              _tmpSchoolBoardId = null;
            } else {
              _tmpSchoolBoardId = _cursor.getString(_cursorIndexOfSchoolBoardId);
            }
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFederalHouseId,_tmpFederalSenateId,_tmpStateSenateId,_tmpStateHouseId,_tmpCountyId,_tmpCityId,_tmpSchoolBoardId,_tmpTier,_tmpAvatarUrl,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getProfile(final String pubKey,
      final Continuation<? super ResidentProfileEntity> $completion) {
    final String _sql = "SELECT * FROM resident_profiles WHERE pubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pubKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ResidentProfileEntity>() {
      @Override
      @Nullable
      public ResidentProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFederalHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalHouseId");
          final int _cursorIndexOfFederalSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalSenateId");
          final int _cursorIndexOfStateSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateSenateId");
          final int _cursorIndexOfStateHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateHouseId");
          final int _cursorIndexOfCountyId = CursorUtil.getColumnIndexOrThrow(_cursor, "countyId");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfSchoolBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolBoardId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFederalHouseId;
            if (_cursor.isNull(_cursorIndexOfFederalHouseId)) {
              _tmpFederalHouseId = null;
            } else {
              _tmpFederalHouseId = _cursor.getString(_cursorIndexOfFederalHouseId);
            }
            final String _tmpFederalSenateId;
            if (_cursor.isNull(_cursorIndexOfFederalSenateId)) {
              _tmpFederalSenateId = null;
            } else {
              _tmpFederalSenateId = _cursor.getString(_cursorIndexOfFederalSenateId);
            }
            final String _tmpStateSenateId;
            if (_cursor.isNull(_cursorIndexOfStateSenateId)) {
              _tmpStateSenateId = null;
            } else {
              _tmpStateSenateId = _cursor.getString(_cursorIndexOfStateSenateId);
            }
            final String _tmpStateHouseId;
            if (_cursor.isNull(_cursorIndexOfStateHouseId)) {
              _tmpStateHouseId = null;
            } else {
              _tmpStateHouseId = _cursor.getString(_cursorIndexOfStateHouseId);
            }
            final String _tmpCountyId;
            if (_cursor.isNull(_cursorIndexOfCountyId)) {
              _tmpCountyId = null;
            } else {
              _tmpCountyId = _cursor.getString(_cursorIndexOfCountyId);
            }
            final String _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getString(_cursorIndexOfCityId);
            }
            final String _tmpSchoolBoardId;
            if (_cursor.isNull(_cursorIndexOfSchoolBoardId)) {
              _tmpSchoolBoardId = null;
            } else {
              _tmpSchoolBoardId = _cursor.getString(_cursorIndexOfSchoolBoardId);
            }
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFederalHouseId,_tmpFederalSenateId,_tmpStateSenateId,_tmpStateHouseId,_tmpCountyId,_tmpCityId,_tmpSchoolBoardId,_tmpTier,_tmpAvatarUrl,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ResidentProfileEntity>> observeProfilesVerifiedBy(final String verifierPubKey) {
    final String _sql = "SELECT * FROM resident_profiles WHERE verifiedByPubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, verifierPubKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"resident_profiles"}, new Callable<List<ResidentProfileEntity>>() {
      @Override
      @NonNull
      public List<ResidentProfileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFederalHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalHouseId");
          final int _cursorIndexOfFederalSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "federalSenateId");
          final int _cursorIndexOfStateSenateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateSenateId");
          final int _cursorIndexOfStateHouseId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateHouseId");
          final int _cursorIndexOfCountyId = CursorUtil.getColumnIndexOrThrow(_cursor, "countyId");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfSchoolBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolBoardId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<ResidentProfileEntity> _result = new ArrayList<ResidentProfileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ResidentProfileEntity _item;
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFederalHouseId;
            if (_cursor.isNull(_cursorIndexOfFederalHouseId)) {
              _tmpFederalHouseId = null;
            } else {
              _tmpFederalHouseId = _cursor.getString(_cursorIndexOfFederalHouseId);
            }
            final String _tmpFederalSenateId;
            if (_cursor.isNull(_cursorIndexOfFederalSenateId)) {
              _tmpFederalSenateId = null;
            } else {
              _tmpFederalSenateId = _cursor.getString(_cursorIndexOfFederalSenateId);
            }
            final String _tmpStateSenateId;
            if (_cursor.isNull(_cursorIndexOfStateSenateId)) {
              _tmpStateSenateId = null;
            } else {
              _tmpStateSenateId = _cursor.getString(_cursorIndexOfStateSenateId);
            }
            final String _tmpStateHouseId;
            if (_cursor.isNull(_cursorIndexOfStateHouseId)) {
              _tmpStateHouseId = null;
            } else {
              _tmpStateHouseId = _cursor.getString(_cursorIndexOfStateHouseId);
            }
            final String _tmpCountyId;
            if (_cursor.isNull(_cursorIndexOfCountyId)) {
              _tmpCountyId = null;
            } else {
              _tmpCountyId = _cursor.getString(_cursorIndexOfCountyId);
            }
            final String _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getString(_cursorIndexOfCityId);
            }
            final String _tmpSchoolBoardId;
            if (_cursor.isNull(_cursorIndexOfSchoolBoardId)) {
              _tmpSchoolBoardId = null;
            } else {
              _tmpSchoolBoardId = _cursor.getString(_cursorIndexOfSchoolBoardId);
            }
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFederalHouseId,_tmpFederalSenateId,_tmpStateSenateId,_tmpStateHouseId,_tmpCountyId,_tmpCityId,_tmpSchoolBoardId,_tmpTier,_tmpAvatarUrl,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
