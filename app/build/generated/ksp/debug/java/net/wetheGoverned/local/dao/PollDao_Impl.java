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
import java.lang.Long;
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
import net.wetheGoverned.local.entity.DistrictPollEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PollDao_Impl implements PollDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DistrictPollEntity> __insertionAdapterOfDistrictPollEntity;

  private final SharedSQLiteStatement __preparedStmtOfApplyOptimisticVote;

  private final SharedSQLiteStatement __preparedStmtOfEvictStalePolls;

  public PollDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDistrictPollEntity = new EntityInsertionAdapter<DistrictPollEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `district_polls` (`id`,`scope`,`districtId`,`localId`,`authorPubKey`,`question`,`optionsJson`,`status`,`createdAt`,`closesAt`,`totalVotes`,`importanceScore`,`userImportanceVote`,`residentVoteOption`,`linkedLegislationId`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistrictPollEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getScope() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getScope());
        }
        statement.bindString(3, entity.getDistrictId());
        if (entity.getLocalId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLocalId());
        }
        statement.bindString(5, entity.getAuthorPubKey());
        statement.bindString(6, entity.getQuestion());
        statement.bindString(7, entity.getOptionsJson());
        statement.bindString(8, entity.getStatus());
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getClosesAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getClosesAt());
        }
        statement.bindLong(11, entity.getTotalVotes());
        statement.bindLong(12, entity.getImportanceScore());
        statement.bindLong(13, entity.getUserImportanceVote());
        if (entity.getResidentVoteOption() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getResidentVoteOption());
        }
        if (entity.getLinkedLegislationId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getLinkedLegislationId());
        }
        statement.bindLong(16, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfApplyOptimisticVote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE district_polls SET residentVoteOption = ?, totalVotes = totalVotes + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfEvictStalePolls = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM district_polls WHERE districtId = ? AND cachedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertPoll(final DistrictPollEntity poll,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistrictPollEntity.insert(poll);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object applyOptimisticVote(final String pollId, final String optionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfApplyOptimisticVote.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, optionId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, pollId);
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
          __preparedStmtOfApplyOptimisticVote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object evictStalePolls(final String districtId, final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEvictStalePolls.acquire();
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
          __preparedStmtOfEvictStalePolls.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DistrictPollEntity>> observePolls(final String districtId) {
    final String _sql = "SELECT * FROM district_polls WHERE districtId = ? ORDER BY importanceScore DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"district_polls"}, new Callable<List<DistrictPollEntity>>() {
      @Override
      @NonNull
      public List<DistrictPollEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfScope = CursorUtil.getColumnIndexOrThrow(_cursor, "scope");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfAuthorPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "authorPubKey");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfClosesAt = CursorUtil.getColumnIndexOrThrow(_cursor, "closesAt");
          final int _cursorIndexOfTotalVotes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVotes");
          final int _cursorIndexOfImportanceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "importanceScore");
          final int _cursorIndexOfUserImportanceVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userImportanceVote");
          final int _cursorIndexOfResidentVoteOption = CursorUtil.getColumnIndexOrThrow(_cursor, "residentVoteOption");
          final int _cursorIndexOfLinkedLegislationId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedLegislationId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<DistrictPollEntity> _result = new ArrayList<DistrictPollEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistrictPollEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpScope;
            if (_cursor.isNull(_cursorIndexOfScope)) {
              _tmpScope = null;
            } else {
              _tmpScope = _cursor.getString(_cursorIndexOfScope);
            }
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
            final String _tmpAuthorPubKey;
            _tmpAuthorPubKey = _cursor.getString(_cursorIndexOfAuthorPubKey);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpClosesAt;
            if (_cursor.isNull(_cursorIndexOfClosesAt)) {
              _tmpClosesAt = null;
            } else {
              _tmpClosesAt = _cursor.getLong(_cursorIndexOfClosesAt);
            }
            final int _tmpTotalVotes;
            _tmpTotalVotes = _cursor.getInt(_cursorIndexOfTotalVotes);
            final int _tmpImportanceScore;
            _tmpImportanceScore = _cursor.getInt(_cursorIndexOfImportanceScore);
            final int _tmpUserImportanceVote;
            _tmpUserImportanceVote = _cursor.getInt(_cursorIndexOfUserImportanceVote);
            final String _tmpResidentVoteOption;
            if (_cursor.isNull(_cursorIndexOfResidentVoteOption)) {
              _tmpResidentVoteOption = null;
            } else {
              _tmpResidentVoteOption = _cursor.getString(_cursorIndexOfResidentVoteOption);
            }
            final String _tmpLinkedLegislationId;
            if (_cursor.isNull(_cursorIndexOfLinkedLegislationId)) {
              _tmpLinkedLegislationId = null;
            } else {
              _tmpLinkedLegislationId = _cursor.getString(_cursorIndexOfLinkedLegislationId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new DistrictPollEntity(_tmpId,_tmpScope,_tmpDistrictId,_tmpLocalId,_tmpAuthorPubKey,_tmpQuestion,_tmpOptionsJson,_tmpStatus,_tmpCreatedAt,_tmpClosesAt,_tmpTotalVotes,_tmpImportanceScore,_tmpUserImportanceVote,_tmpResidentVoteOption,_tmpLinkedLegislationId,_tmpCachedAt);
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
  public Object getPollsSync(final String districtId,
      final Continuation<? super List<DistrictPollEntity>> $completion) {
    final String _sql = "SELECT * FROM district_polls WHERE districtId = ? ORDER BY importanceScore DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DistrictPollEntity>>() {
      @Override
      @NonNull
      public List<DistrictPollEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfScope = CursorUtil.getColumnIndexOrThrow(_cursor, "scope");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfAuthorPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "authorPubKey");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfClosesAt = CursorUtil.getColumnIndexOrThrow(_cursor, "closesAt");
          final int _cursorIndexOfTotalVotes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVotes");
          final int _cursorIndexOfImportanceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "importanceScore");
          final int _cursorIndexOfUserImportanceVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userImportanceVote");
          final int _cursorIndexOfResidentVoteOption = CursorUtil.getColumnIndexOrThrow(_cursor, "residentVoteOption");
          final int _cursorIndexOfLinkedLegislationId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedLegislationId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<DistrictPollEntity> _result = new ArrayList<DistrictPollEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistrictPollEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpScope;
            if (_cursor.isNull(_cursorIndexOfScope)) {
              _tmpScope = null;
            } else {
              _tmpScope = _cursor.getString(_cursorIndexOfScope);
            }
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
            final String _tmpAuthorPubKey;
            _tmpAuthorPubKey = _cursor.getString(_cursorIndexOfAuthorPubKey);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpClosesAt;
            if (_cursor.isNull(_cursorIndexOfClosesAt)) {
              _tmpClosesAt = null;
            } else {
              _tmpClosesAt = _cursor.getLong(_cursorIndexOfClosesAt);
            }
            final int _tmpTotalVotes;
            _tmpTotalVotes = _cursor.getInt(_cursorIndexOfTotalVotes);
            final int _tmpImportanceScore;
            _tmpImportanceScore = _cursor.getInt(_cursorIndexOfImportanceScore);
            final int _tmpUserImportanceVote;
            _tmpUserImportanceVote = _cursor.getInt(_cursorIndexOfUserImportanceVote);
            final String _tmpResidentVoteOption;
            if (_cursor.isNull(_cursorIndexOfResidentVoteOption)) {
              _tmpResidentVoteOption = null;
            } else {
              _tmpResidentVoteOption = _cursor.getString(_cursorIndexOfResidentVoteOption);
            }
            final String _tmpLinkedLegislationId;
            if (_cursor.isNull(_cursorIndexOfLinkedLegislationId)) {
              _tmpLinkedLegislationId = null;
            } else {
              _tmpLinkedLegislationId = _cursor.getString(_cursorIndexOfLinkedLegislationId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new DistrictPollEntity(_tmpId,_tmpScope,_tmpDistrictId,_tmpLocalId,_tmpAuthorPubKey,_tmpQuestion,_tmpOptionsJson,_tmpStatus,_tmpCreatedAt,_tmpClosesAt,_tmpTotalVotes,_tmpImportanceScore,_tmpUserImportanceVote,_tmpResidentVoteOption,_tmpLinkedLegislationId,_tmpCachedAt);
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
  public Object getAllPollsAcrossDistricts(
      final Continuation<? super List<DistrictPollEntity>> $completion) {
    final String _sql = "SELECT * FROM district_polls ORDER BY importanceScore DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DistrictPollEntity>>() {
      @Override
      @NonNull
      public List<DistrictPollEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfScope = CursorUtil.getColumnIndexOrThrow(_cursor, "scope");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfAuthorPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "authorPubKey");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfClosesAt = CursorUtil.getColumnIndexOrThrow(_cursor, "closesAt");
          final int _cursorIndexOfTotalVotes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVotes");
          final int _cursorIndexOfImportanceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "importanceScore");
          final int _cursorIndexOfUserImportanceVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userImportanceVote");
          final int _cursorIndexOfResidentVoteOption = CursorUtil.getColumnIndexOrThrow(_cursor, "residentVoteOption");
          final int _cursorIndexOfLinkedLegislationId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedLegislationId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<DistrictPollEntity> _result = new ArrayList<DistrictPollEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DistrictPollEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpScope;
            if (_cursor.isNull(_cursorIndexOfScope)) {
              _tmpScope = null;
            } else {
              _tmpScope = _cursor.getString(_cursorIndexOfScope);
            }
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
            final String _tmpAuthorPubKey;
            _tmpAuthorPubKey = _cursor.getString(_cursorIndexOfAuthorPubKey);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpClosesAt;
            if (_cursor.isNull(_cursorIndexOfClosesAt)) {
              _tmpClosesAt = null;
            } else {
              _tmpClosesAt = _cursor.getLong(_cursorIndexOfClosesAt);
            }
            final int _tmpTotalVotes;
            _tmpTotalVotes = _cursor.getInt(_cursorIndexOfTotalVotes);
            final int _tmpImportanceScore;
            _tmpImportanceScore = _cursor.getInt(_cursorIndexOfImportanceScore);
            final int _tmpUserImportanceVote;
            _tmpUserImportanceVote = _cursor.getInt(_cursorIndexOfUserImportanceVote);
            final String _tmpResidentVoteOption;
            if (_cursor.isNull(_cursorIndexOfResidentVoteOption)) {
              _tmpResidentVoteOption = null;
            } else {
              _tmpResidentVoteOption = _cursor.getString(_cursorIndexOfResidentVoteOption);
            }
            final String _tmpLinkedLegislationId;
            if (_cursor.isNull(_cursorIndexOfLinkedLegislationId)) {
              _tmpLinkedLegislationId = null;
            } else {
              _tmpLinkedLegislationId = _cursor.getString(_cursorIndexOfLinkedLegislationId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new DistrictPollEntity(_tmpId,_tmpScope,_tmpDistrictId,_tmpLocalId,_tmpAuthorPubKey,_tmpQuestion,_tmpOptionsJson,_tmpStatus,_tmpCreatedAt,_tmpClosesAt,_tmpTotalVotes,_tmpImportanceScore,_tmpUserImportanceVote,_tmpResidentVoteOption,_tmpLinkedLegislationId,_tmpCachedAt);
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
  public Object getPoll(final String pollId,
      final Continuation<? super DistrictPollEntity> $completion) {
    final String _sql = "SELECT * FROM district_polls WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pollId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistrictPollEntity>() {
      @Override
      @Nullable
      public DistrictPollEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfScope = CursorUtil.getColumnIndexOrThrow(_cursor, "scope");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfAuthorPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "authorPubKey");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfClosesAt = CursorUtil.getColumnIndexOrThrow(_cursor, "closesAt");
          final int _cursorIndexOfTotalVotes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVotes");
          final int _cursorIndexOfImportanceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "importanceScore");
          final int _cursorIndexOfUserImportanceVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userImportanceVote");
          final int _cursorIndexOfResidentVoteOption = CursorUtil.getColumnIndexOrThrow(_cursor, "residentVoteOption");
          final int _cursorIndexOfLinkedLegislationId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedLegislationId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final DistrictPollEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpScope;
            if (_cursor.isNull(_cursorIndexOfScope)) {
              _tmpScope = null;
            } else {
              _tmpScope = _cursor.getString(_cursorIndexOfScope);
            }
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
            final String _tmpAuthorPubKey;
            _tmpAuthorPubKey = _cursor.getString(_cursorIndexOfAuthorPubKey);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpClosesAt;
            if (_cursor.isNull(_cursorIndexOfClosesAt)) {
              _tmpClosesAt = null;
            } else {
              _tmpClosesAt = _cursor.getLong(_cursorIndexOfClosesAt);
            }
            final int _tmpTotalVotes;
            _tmpTotalVotes = _cursor.getInt(_cursorIndexOfTotalVotes);
            final int _tmpImportanceScore;
            _tmpImportanceScore = _cursor.getInt(_cursorIndexOfImportanceScore);
            final int _tmpUserImportanceVote;
            _tmpUserImportanceVote = _cursor.getInt(_cursorIndexOfUserImportanceVote);
            final String _tmpResidentVoteOption;
            if (_cursor.isNull(_cursorIndexOfResidentVoteOption)) {
              _tmpResidentVoteOption = null;
            } else {
              _tmpResidentVoteOption = _cursor.getString(_cursorIndexOfResidentVoteOption);
            }
            final String _tmpLinkedLegislationId;
            if (_cursor.isNull(_cursorIndexOfLinkedLegislationId)) {
              _tmpLinkedLegislationId = null;
            } else {
              _tmpLinkedLegislationId = _cursor.getString(_cursorIndexOfLinkedLegislationId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new DistrictPollEntity(_tmpId,_tmpScope,_tmpDistrictId,_tmpLocalId,_tmpAuthorPubKey,_tmpQuestion,_tmpOptionsJson,_tmpStatus,_tmpCreatedAt,_tmpClosesAt,_tmpTotalVotes,_tmpImportanceScore,_tmpUserImportanceVote,_tmpResidentVoteOption,_tmpLinkedLegislationId,_tmpCachedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
