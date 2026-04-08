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
import net.wetheGoverned.local.entity.CandidateManifestoEntity;
import net.wetheGoverned.local.entity.ManifestoQuestionEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ManifestoDao_Impl implements ManifestoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CandidateManifestoEntity> __insertionAdapterOfCandidateManifestoEntity;

  private final EntityInsertionAdapter<ManifestoQuestionEntity> __insertionAdapterOfManifestoQuestionEntity;

  private final SharedSQLiteStatement __preparedStmtOfRecordAnswer;

  public ManifestoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCandidateManifestoEntity = new EntityInsertionAdapter<CandidateManifestoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `candidate_manifestos` (`id`,`candidatePubKey`,`districtId`,`title`,`body`,`publishedAt`,`cachedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CandidateManifestoEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getCandidatePubKey());
        statement.bindString(3, entity.getDistrictId());
        statement.bindString(4, entity.getTitle());
        statement.bindString(5, entity.getBody());
        statement.bindLong(6, entity.getPublishedAt());
        statement.bindLong(7, entity.getCachedAt());
      }
    };
    this.__insertionAdapterOfManifestoQuestionEntity = new EntityInsertionAdapter<ManifestoQuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `manifesto_questions` (`id`,`manifestoId`,`askerPubKey`,`text`,`askedAt`,`answer`,`answeredAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ManifestoQuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getManifestoId());
        statement.bindString(3, entity.getAskerPubKey());
        statement.bindString(4, entity.getText());
        statement.bindLong(5, entity.getAskedAt());
        if (entity.getAnswer() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAnswer());
        }
        if (entity.getAnsweredAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getAnsweredAt());
        }
      }
    };
    this.__preparedStmtOfRecordAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE manifesto_questions SET answer = ?, answeredAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertManifesto(final CandidateManifestoEntity manifesto,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCandidateManifestoEntity.insert(manifesto);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertQuestion(final ManifestoQuestionEntity question,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfManifestoQuestionEntity.insert(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object recordAnswer(final String questionId, final String answer, final long answeredAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRecordAnswer.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, answer);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, answeredAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, questionId);
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
          __preparedStmtOfRecordAnswer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CandidateManifestoEntity>> observeManifestos(final String districtId) {
    final String _sql = "\n"
            + "        SELECT * FROM candidate_manifestos\n"
            + "        WHERE districtId = ?\n"
            + "        ORDER BY publishedAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"candidate_manifestos"}, new Callable<List<CandidateManifestoEntity>>() {
      @Override
      @NonNull
      public List<CandidateManifestoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCandidatePubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "candidatePubKey");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<CandidateManifestoEntity> _result = new ArrayList<CandidateManifestoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CandidateManifestoEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCandidatePubKey;
            _tmpCandidatePubKey = _cursor.getString(_cursorIndexOfCandidatePubKey);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final long _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getLong(_cursorIndexOfPublishedAt);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new CandidateManifestoEntity(_tmpId,_tmpCandidatePubKey,_tmpDistrictId,_tmpTitle,_tmpBody,_tmpPublishedAt,_tmpCachedAt);
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
  public Object getManifesto(final String manifestoId,
      final Continuation<? super CandidateManifestoEntity> $completion) {
    final String _sql = "SELECT * FROM candidate_manifestos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, manifestoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CandidateManifestoEntity>() {
      @Override
      @Nullable
      public CandidateManifestoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCandidatePubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "candidatePubKey");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final CandidateManifestoEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCandidatePubKey;
            _tmpCandidatePubKey = _cursor.getString(_cursorIndexOfCandidatePubKey);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpBody;
            _tmpBody = _cursor.getString(_cursorIndexOfBody);
            final long _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getLong(_cursorIndexOfPublishedAt);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new CandidateManifestoEntity(_tmpId,_tmpCandidatePubKey,_tmpDistrictId,_tmpTitle,_tmpBody,_tmpPublishedAt,_tmpCachedAt);
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
  public Object getQuestions(final String manifestoId,
      final Continuation<? super List<ManifestoQuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM manifesto_questions WHERE manifestoId = ? ORDER BY askedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, manifestoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ManifestoQuestionEntity>>() {
      @Override
      @NonNull
      public List<ManifestoQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfManifestoId = CursorUtil.getColumnIndexOrThrow(_cursor, "manifestoId");
          final int _cursorIndexOfAskerPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "askerPubKey");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfAskedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "askedAt");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnsweredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredAt");
          final List<ManifestoQuestionEntity> _result = new ArrayList<ManifestoQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManifestoQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpManifestoId;
            _tmpManifestoId = _cursor.getString(_cursorIndexOfManifestoId);
            final String _tmpAskerPubKey;
            _tmpAskerPubKey = _cursor.getString(_cursorIndexOfAskerPubKey);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpAskedAt;
            _tmpAskedAt = _cursor.getLong(_cursorIndexOfAskedAt);
            final String _tmpAnswer;
            if (_cursor.isNull(_cursorIndexOfAnswer)) {
              _tmpAnswer = null;
            } else {
              _tmpAnswer = _cursor.getString(_cursorIndexOfAnswer);
            }
            final Long _tmpAnsweredAt;
            if (_cursor.isNull(_cursorIndexOfAnsweredAt)) {
              _tmpAnsweredAt = null;
            } else {
              _tmpAnsweredAt = _cursor.getLong(_cursorIndexOfAnsweredAt);
            }
            _item = new ManifestoQuestionEntity(_tmpId,_tmpManifestoId,_tmpAskerPubKey,_tmpText,_tmpAskedAt,_tmpAnswer,_tmpAnsweredAt);
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
