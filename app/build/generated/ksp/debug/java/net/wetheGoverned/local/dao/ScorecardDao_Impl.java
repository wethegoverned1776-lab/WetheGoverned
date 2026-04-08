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
import net.wetheGoverned.local.entity.RepresentativeScorecardEntity;
import net.wetheGoverned.local.entity.ScorecardCategoryEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScorecardDao_Impl implements ScorecardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RepresentativeScorecardEntity> __insertionAdapterOfRepresentativeScorecardEntity;

  private final EntityInsertionAdapter<ScorecardCategoryEntity> __insertionAdapterOfScorecardCategoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCategoriesForDistrict;

  public ScorecardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRepresentativeScorecardEntity = new EntityInsertionAdapter<RepresentativeScorecardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `representative_scorecards` (`districtId`,`representativePubKey`,`name`,`party`,`overallScore`,`lastUpdated`,`cachedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RepresentativeScorecardEntity entity) {
        statement.bindString(1, entity.getDistrictId());
        statement.bindString(2, entity.getRepresentativePubKey());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getParty());
        statement.bindLong(5, entity.getOverallScore());
        statement.bindLong(6, entity.getLastUpdated());
        statement.bindLong(7, entity.getCachedAt());
      }
    };
    this.__insertionAdapterOfScorecardCategoryEntity = new EntityInsertionAdapter<ScorecardCategoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scorecard_categories` (`rowId`,`districtId`,`categoryName`,`officialValue`,`residentReportedValue`,`score`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScorecardCategoryEntity entity) {
        statement.bindLong(1, entity.getRowId());
        statement.bindString(2, entity.getDistrictId());
        statement.bindString(3, entity.getCategoryName());
        statement.bindString(4, entity.getOfficialValue());
        if (entity.getResidentReportedValue() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getResidentReportedValue());
        }
        statement.bindLong(6, entity.getScore());
      }
    };
    this.__preparedStmtOfDeleteCategoriesForDistrict = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scorecard_categories WHERE districtId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertScorecard(final RepresentativeScorecardEntity scorecard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRepresentativeScorecardEntity.insert(scorecard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertCategories(final List<ScorecardCategoryEntity> categories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScorecardCategoryEntity.insert(categories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCategoriesForDistrict(final String districtId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCategoriesForDistrict.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, districtId);
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
          __preparedStmtOfDeleteCategoriesForDistrict.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<RepresentativeScorecardEntity> observeScorecard(final String districtId) {
    final String _sql = "SELECT * FROM representative_scorecards WHERE districtId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"representative_scorecards"}, new Callable<RepresentativeScorecardEntity>() {
      @Override
      @Nullable
      public RepresentativeScorecardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfRepresentativePubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "representativePubKey");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfParty = CursorUtil.getColumnIndexOrThrow(_cursor, "party");
          final int _cursorIndexOfOverallScore = CursorUtil.getColumnIndexOrThrow(_cursor, "overallScore");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final RepresentativeScorecardEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpRepresentativePubKey;
            _tmpRepresentativePubKey = _cursor.getString(_cursorIndexOfRepresentativePubKey);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpParty;
            _tmpParty = _cursor.getString(_cursorIndexOfParty);
            final int _tmpOverallScore;
            _tmpOverallScore = _cursor.getInt(_cursorIndexOfOverallScore);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new RepresentativeScorecardEntity(_tmpDistrictId,_tmpRepresentativePubKey,_tmpName,_tmpParty,_tmpOverallScore,_tmpLastUpdated,_tmpCachedAt);
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
  public Object getScorecard(final String districtId,
      final Continuation<? super RepresentativeScorecardEntity> $completion) {
    final String _sql = "SELECT * FROM representative_scorecards WHERE districtId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RepresentativeScorecardEntity>() {
      @Override
      @Nullable
      public RepresentativeScorecardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfRepresentativePubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "representativePubKey");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfParty = CursorUtil.getColumnIndexOrThrow(_cursor, "party");
          final int _cursorIndexOfOverallScore = CursorUtil.getColumnIndexOrThrow(_cursor, "overallScore");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final RepresentativeScorecardEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpRepresentativePubKey;
            _tmpRepresentativePubKey = _cursor.getString(_cursorIndexOfRepresentativePubKey);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpParty;
            _tmpParty = _cursor.getString(_cursorIndexOfParty);
            final int _tmpOverallScore;
            _tmpOverallScore = _cursor.getInt(_cursorIndexOfOverallScore);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new RepresentativeScorecardEntity(_tmpDistrictId,_tmpRepresentativePubKey,_tmpName,_tmpParty,_tmpOverallScore,_tmpLastUpdated,_tmpCachedAt);
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
  public Object getCategoriesForDistrict(final String districtId,
      final Continuation<? super List<ScorecardCategoryEntity>> $completion) {
    final String _sql = "SELECT * FROM scorecard_categories WHERE districtId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScorecardCategoryEntity>>() {
      @Override
      @NonNull
      public List<ScorecardCategoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfRowId = CursorUtil.getColumnIndexOrThrow(_cursor, "rowId");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfOfficialValue = CursorUtil.getColumnIndexOrThrow(_cursor, "officialValue");
          final int _cursorIndexOfResidentReportedValue = CursorUtil.getColumnIndexOrThrow(_cursor, "residentReportedValue");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final List<ScorecardCategoryEntity> _result = new ArrayList<ScorecardCategoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScorecardCategoryEntity _item;
            final long _tmpRowId;
            _tmpRowId = _cursor.getLong(_cursorIndexOfRowId);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final String _tmpOfficialValue;
            _tmpOfficialValue = _cursor.getString(_cursorIndexOfOfficialValue);
            final String _tmpResidentReportedValue;
            if (_cursor.isNull(_cursorIndexOfResidentReportedValue)) {
              _tmpResidentReportedValue = null;
            } else {
              _tmpResidentReportedValue = _cursor.getString(_cursorIndexOfResidentReportedValue);
            }
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            _item = new ScorecardCategoryEntity(_tmpRowId,_tmpDistrictId,_tmpCategoryName,_tmpOfficialValue,_tmpResidentReportedValue,_tmpScore);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
