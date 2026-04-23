package net.wetheGoverned.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
import net.wetheGoverned.local.entity.DistrictMetricEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MetricDao_Impl implements MetricDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DistrictMetricEntity> __insertionAdapterOfDistrictMetricEntity;

  private final SharedSQLiteStatement __preparedStmtOfEvictStaleMetrics;

  public MetricDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDistrictMetricEntity = new EntityInsertionAdapter<DistrictMetricEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `district_metrics` (`id`,`districtId`,`category`,`name`,`officialValue`,`residentValue`,`unit`,`source`,`reportedAt`,`reporterPubKey`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistrictMetricEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDistrictId());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getName());
        statement.bindString(5, entity.getOfficialValue());
        if (entity.getResidentValue() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getResidentValue());
        }
        statement.bindString(7, entity.getUnit());
        statement.bindString(8, entity.getSource());
        statement.bindLong(9, entity.getReportedAt());
        if (entity.getReporterPubKey() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getReporterPubKey());
        }
        statement.bindLong(11, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfEvictStaleMetrics = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM district_metrics WHERE districtId = ? AND cachedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertMetric(final DistrictMetricEntity metric,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistrictMetricEntity.insert(metric);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertMetrics(final List<DistrictMetricEntity> metrics,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistrictMetricEntity.insert(metrics);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object evictStaleMetrics(final String districtId, final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEvictStaleMetrics.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, districtId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, before);
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
          __preparedStmtOfEvictStaleMetrics.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DistrictMetricEntity>> observeMetrics(final String districtId) {
    final String _sql = "SELECT * FROM district_metrics WHERE districtId = ? ORDER BY reportedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"district_metrics"}, new Callable<List<DistrictMetricEntity>>() {
      @Override
      @NonNull
      public List<DistrictMetricEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfOfficialValue = CursorUtil.getColumnIndexOrThrow(_cursor, "officialValue");
          final int _cursorIndexOfResidentValue = CursorUtil.getColumnIndexOrThrow(_cursor, "residentValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfReportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reportedAt");
          final int _cursorIndexOfReporterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "reporterPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<DistrictMetricEntity> _result = new ArrayList<DistrictMetricEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistrictMetricEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpOfficialValue;
            _tmpOfficialValue = _cursor.getString(_cursorIndexOfOfficialValue);
            final String _tmpResidentValue;
            if (_cursor.isNull(_cursorIndexOfResidentValue)) {
              _tmpResidentValue = null;
            } else {
              _tmpResidentValue = _cursor.getString(_cursorIndexOfResidentValue);
            }
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpReportedAt;
            _tmpReportedAt = _cursor.getLong(_cursorIndexOfReportedAt);
            final String _tmpReporterPubKey;
            if (_cursor.isNull(_cursorIndexOfReporterPubKey)) {
              _tmpReporterPubKey = null;
            } else {
              _tmpReporterPubKey = _cursor.getString(_cursorIndexOfReporterPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new DistrictMetricEntity(_tmpId,_tmpDistrictId,_tmpCategory,_tmpName,_tmpOfficialValue,_tmpResidentValue,_tmpUnit,_tmpSource,_tmpReportedAt,_tmpReporterPubKey,_tmpCachedAt);
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
