package net.wetheGoverned.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import net.wetheGoverned.local.dao.DistrictDao;
import net.wetheGoverned.local.dao.DistrictDao_Impl;
import net.wetheGoverned.local.dao.ManifestoDao;
import net.wetheGoverned.local.dao.ManifestoDao_Impl;
import net.wetheGoverned.local.dao.MetricDao;
import net.wetheGoverned.local.dao.MetricDao_Impl;
import net.wetheGoverned.local.dao.PendingEventDao;
import net.wetheGoverned.local.dao.PendingEventDao_Impl;
import net.wetheGoverned.local.dao.PollDao;
import net.wetheGoverned.local.dao.PollDao_Impl;
import net.wetheGoverned.local.dao.ResidentProfileDao;
import net.wetheGoverned.local.dao.ResidentProfileDao_Impl;
import net.wetheGoverned.local.dao.ScorecardDao;
import net.wetheGoverned.local.dao.ScorecardDao_Impl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CivicDatabase_Impl extends CivicDatabase {
  private volatile DistrictDao _districtDao;

  private volatile ResidentProfileDao _residentProfileDao;

  private volatile PollDao _pollDao;

  private volatile ScorecardDao _scorecardDao;

  private volatile ManifestoDao _manifestoDao;

  private volatile MetricDao _metricDao;

  private volatile PendingEventDao _pendingEventDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `districts` (`id` TEXT NOT NULL, `state` TEXT NOT NULL, `districtNumber` INTEGER NOT NULL, `displayName` TEXT NOT NULL, `representativeId` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `resident_profiles` (`pubKey` TEXT NOT NULL, `displayName` TEXT NOT NULL, `districtId` TEXT NOT NULL, `tier` TEXT NOT NULL, `avatarUrl` TEXT, `bio` TEXT, `joinedAt` INTEGER NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`pubKey`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `district_polls` (`id` TEXT NOT NULL, `districtId` TEXT NOT NULL, `authorPubKey` TEXT NOT NULL, `question` TEXT NOT NULL, `optionsJson` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `closesAt` INTEGER, `totalVotes` INTEGER NOT NULL, `residentVoteOption` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `representative_scorecards` (`districtId` TEXT NOT NULL, `representativePubKey` TEXT NOT NULL, `name` TEXT NOT NULL, `party` TEXT NOT NULL, `overallScore` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`districtId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scorecard_categories` (`rowId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `districtId` TEXT NOT NULL, `categoryName` TEXT NOT NULL, `officialValue` TEXT NOT NULL, `residentReportedValue` TEXT, `score` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `candidate_manifestos` (`id` TEXT NOT NULL, `candidatePubKey` TEXT NOT NULL, `districtId` TEXT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `publishedAt` INTEGER NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `manifesto_questions` (`id` TEXT NOT NULL, `manifestoId` TEXT NOT NULL, `askerPubKey` TEXT NOT NULL, `text` TEXT NOT NULL, `askedAt` INTEGER NOT NULL, `answer` TEXT, `answeredAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `district_metrics` (`id` TEXT NOT NULL, `districtId` TEXT NOT NULL, `category` TEXT NOT NULL, `name` TEXT NOT NULL, `officialValue` TEXT NOT NULL, `residentValue` TEXT, `unit` TEXT NOT NULL, `source` TEXT NOT NULL, `reportedAt` INTEGER NOT NULL, `reporterPubKey` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_civic_events` (`eventId` TEXT NOT NULL, `kind` INTEGER NOT NULL, `contentJson` TEXT NOT NULL, `sig` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, PRIMARY KEY(`eventId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'abb81efbe14f73377797b4107d10aa5b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `districts`");
        db.execSQL("DROP TABLE IF EXISTS `resident_profiles`");
        db.execSQL("DROP TABLE IF EXISTS `district_polls`");
        db.execSQL("DROP TABLE IF EXISTS `representative_scorecards`");
        db.execSQL("DROP TABLE IF EXISTS `scorecard_categories`");
        db.execSQL("DROP TABLE IF EXISTS `candidate_manifestos`");
        db.execSQL("DROP TABLE IF EXISTS `manifesto_questions`");
        db.execSQL("DROP TABLE IF EXISTS `district_metrics`");
        db.execSQL("DROP TABLE IF EXISTS `pending_civic_events`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDistricts = new HashMap<String, TableInfo.Column>(6);
        _columnsDistricts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistricts.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistricts.put("districtNumber", new TableInfo.Column("districtNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistricts.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistricts.put("representativeId", new TableInfo.Column("representativeId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistricts.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDistricts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDistricts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistricts = new TableInfo("districts", _columnsDistricts, _foreignKeysDistricts, _indicesDistricts);
        final TableInfo _existingDistricts = TableInfo.read(db, "districts");
        if (!_infoDistricts.equals(_existingDistricts)) {
          return new RoomOpenHelper.ValidationResult(false, "districts(net.wetheGoverned.local.entity.DistrictEntity).\n"
                  + " Expected:\n" + _infoDistricts + "\n"
                  + " Found:\n" + _existingDistricts);
        }
        final HashMap<String, TableInfo.Column> _columnsResidentProfiles = new HashMap<String, TableInfo.Column>(8);
        _columnsResidentProfiles.put("pubKey", new TableInfo.Column("pubKey", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("tier", new TableInfo.Column("tier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("avatarUrl", new TableInfo.Column("avatarUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("bio", new TableInfo.Column("bio", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("joinedAt", new TableInfo.Column("joinedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResidentProfiles.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysResidentProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesResidentProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoResidentProfiles = new TableInfo("resident_profiles", _columnsResidentProfiles, _foreignKeysResidentProfiles, _indicesResidentProfiles);
        final TableInfo _existingResidentProfiles = TableInfo.read(db, "resident_profiles");
        if (!_infoResidentProfiles.equals(_existingResidentProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "resident_profiles(net.wetheGoverned.local.entity.ResidentProfileEntity).\n"
                  + " Expected:\n" + _infoResidentProfiles + "\n"
                  + " Found:\n" + _existingResidentProfiles);
        }
        final HashMap<String, TableInfo.Column> _columnsDistrictPolls = new HashMap<String, TableInfo.Column>(11);
        _columnsDistrictPolls.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("authorPubKey", new TableInfo.Column("authorPubKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("question", new TableInfo.Column("question", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("optionsJson", new TableInfo.Column("optionsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("closesAt", new TableInfo.Column("closesAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("totalVotes", new TableInfo.Column("totalVotes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("residentVoteOption", new TableInfo.Column("residentVoteOption", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictPolls.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDistrictPolls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDistrictPolls = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistrictPolls = new TableInfo("district_polls", _columnsDistrictPolls, _foreignKeysDistrictPolls, _indicesDistrictPolls);
        final TableInfo _existingDistrictPolls = TableInfo.read(db, "district_polls");
        if (!_infoDistrictPolls.equals(_existingDistrictPolls)) {
          return new RoomOpenHelper.ValidationResult(false, "district_polls(net.wetheGoverned.local.entity.DistrictPollEntity).\n"
                  + " Expected:\n" + _infoDistrictPolls + "\n"
                  + " Found:\n" + _existingDistrictPolls);
        }
        final HashMap<String, TableInfo.Column> _columnsRepresentativeScorecards = new HashMap<String, TableInfo.Column>(7);
        _columnsRepresentativeScorecards.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("representativePubKey", new TableInfo.Column("representativePubKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("party", new TableInfo.Column("party", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("overallScore", new TableInfo.Column("overallScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRepresentativeScorecards.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRepresentativeScorecards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRepresentativeScorecards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRepresentativeScorecards = new TableInfo("representative_scorecards", _columnsRepresentativeScorecards, _foreignKeysRepresentativeScorecards, _indicesRepresentativeScorecards);
        final TableInfo _existingRepresentativeScorecards = TableInfo.read(db, "representative_scorecards");
        if (!_infoRepresentativeScorecards.equals(_existingRepresentativeScorecards)) {
          return new RoomOpenHelper.ValidationResult(false, "representative_scorecards(net.wetheGoverned.local.entity.RepresentativeScorecardEntity).\n"
                  + " Expected:\n" + _infoRepresentativeScorecards + "\n"
                  + " Found:\n" + _existingRepresentativeScorecards);
        }
        final HashMap<String, TableInfo.Column> _columnsScorecardCategories = new HashMap<String, TableInfo.Column>(6);
        _columnsScorecardCategories.put("rowId", new TableInfo.Column("rowId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScorecardCategories.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScorecardCategories.put("categoryName", new TableInfo.Column("categoryName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScorecardCategories.put("officialValue", new TableInfo.Column("officialValue", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScorecardCategories.put("residentReportedValue", new TableInfo.Column("residentReportedValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScorecardCategories.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScorecardCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScorecardCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScorecardCategories = new TableInfo("scorecard_categories", _columnsScorecardCategories, _foreignKeysScorecardCategories, _indicesScorecardCategories);
        final TableInfo _existingScorecardCategories = TableInfo.read(db, "scorecard_categories");
        if (!_infoScorecardCategories.equals(_existingScorecardCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "scorecard_categories(net.wetheGoverned.local.entity.ScorecardCategoryEntity).\n"
                  + " Expected:\n" + _infoScorecardCategories + "\n"
                  + " Found:\n" + _existingScorecardCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsCandidateManifestos = new HashMap<String, TableInfo.Column>(7);
        _columnsCandidateManifestos.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("candidatePubKey", new TableInfo.Column("candidatePubKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("publishedAt", new TableInfo.Column("publishedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCandidateManifestos.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCandidateManifestos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCandidateManifestos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCandidateManifestos = new TableInfo("candidate_manifestos", _columnsCandidateManifestos, _foreignKeysCandidateManifestos, _indicesCandidateManifestos);
        final TableInfo _existingCandidateManifestos = TableInfo.read(db, "candidate_manifestos");
        if (!_infoCandidateManifestos.equals(_existingCandidateManifestos)) {
          return new RoomOpenHelper.ValidationResult(false, "candidate_manifestos(net.wetheGoverned.local.entity.CandidateManifestoEntity).\n"
                  + " Expected:\n" + _infoCandidateManifestos + "\n"
                  + " Found:\n" + _existingCandidateManifestos);
        }
        final HashMap<String, TableInfo.Column> _columnsManifestoQuestions = new HashMap<String, TableInfo.Column>(7);
        _columnsManifestoQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("manifestoId", new TableInfo.Column("manifestoId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("askerPubKey", new TableInfo.Column("askerPubKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("askedAt", new TableInfo.Column("askedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("answer", new TableInfo.Column("answer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManifestoQuestions.put("answeredAt", new TableInfo.Column("answeredAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysManifestoQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesManifestoQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoManifestoQuestions = new TableInfo("manifesto_questions", _columnsManifestoQuestions, _foreignKeysManifestoQuestions, _indicesManifestoQuestions);
        final TableInfo _existingManifestoQuestions = TableInfo.read(db, "manifesto_questions");
        if (!_infoManifestoQuestions.equals(_existingManifestoQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "manifesto_questions(net.wetheGoverned.local.entity.ManifestoQuestionEntity).\n"
                  + " Expected:\n" + _infoManifestoQuestions + "\n"
                  + " Found:\n" + _existingManifestoQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsDistrictMetrics = new HashMap<String, TableInfo.Column>(11);
        _columnsDistrictMetrics.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("districtId", new TableInfo.Column("districtId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("officialValue", new TableInfo.Column("officialValue", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("residentValue", new TableInfo.Column("residentValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("reportedAt", new TableInfo.Column("reportedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("reporterPubKey", new TableInfo.Column("reporterPubKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDistrictMetrics.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDistrictMetrics = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDistrictMetrics = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDistrictMetrics = new TableInfo("district_metrics", _columnsDistrictMetrics, _foreignKeysDistrictMetrics, _indicesDistrictMetrics);
        final TableInfo _existingDistrictMetrics = TableInfo.read(db, "district_metrics");
        if (!_infoDistrictMetrics.equals(_existingDistrictMetrics)) {
          return new RoomOpenHelper.ValidationResult(false, "district_metrics(net.wetheGoverned.local.entity.DistrictMetricEntity).\n"
                  + " Expected:\n" + _infoDistrictMetrics + "\n"
                  + " Found:\n" + _existingDistrictMetrics);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingCivicEvents = new HashMap<String, TableInfo.Column>(6);
        _columnsPendingCivicEvents.put("eventId", new TableInfo.Column("eventId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingCivicEvents.put("kind", new TableInfo.Column("kind", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingCivicEvents.put("contentJson", new TableInfo.Column("contentJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingCivicEvents.put("sig", new TableInfo.Column("sig", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingCivicEvents.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingCivicEvents.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingCivicEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingCivicEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingCivicEvents = new TableInfo("pending_civic_events", _columnsPendingCivicEvents, _foreignKeysPendingCivicEvents, _indicesPendingCivicEvents);
        final TableInfo _existingPendingCivicEvents = TableInfo.read(db, "pending_civic_events");
        if (!_infoPendingCivicEvents.equals(_existingPendingCivicEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_civic_events(net.wetheGoverned.local.entity.PendingCivicEventEntity).\n"
                  + " Expected:\n" + _infoPendingCivicEvents + "\n"
                  + " Found:\n" + _existingPendingCivicEvents);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "abb81efbe14f73377797b4107d10aa5b", "219c2fb0eefd1c2b0c7354b8be0fe77f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "districts","resident_profiles","district_polls","representative_scorecards","scorecard_categories","candidate_manifestos","manifesto_questions","district_metrics","pending_civic_events");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `districts`");
      _db.execSQL("DELETE FROM `resident_profiles`");
      _db.execSQL("DELETE FROM `district_polls`");
      _db.execSQL("DELETE FROM `representative_scorecards`");
      _db.execSQL("DELETE FROM `scorecard_categories`");
      _db.execSQL("DELETE FROM `candidate_manifestos`");
      _db.execSQL("DELETE FROM `manifesto_questions`");
      _db.execSQL("DELETE FROM `district_metrics`");
      _db.execSQL("DELETE FROM `pending_civic_events`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DistrictDao.class, DistrictDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ResidentProfileDao.class, ResidentProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PollDao.class, PollDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScorecardDao.class, ScorecardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ManifestoDao.class, ManifestoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MetricDao.class, MetricDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingEventDao.class, PendingEventDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DistrictDao districtDao() {
    if (_districtDao != null) {
      return _districtDao;
    } else {
      synchronized(this) {
        if(_districtDao == null) {
          _districtDao = new DistrictDao_Impl(this);
        }
        return _districtDao;
      }
    }
  }

  @Override
  public ResidentProfileDao residentProfileDao() {
    if (_residentProfileDao != null) {
      return _residentProfileDao;
    } else {
      synchronized(this) {
        if(_residentProfileDao == null) {
          _residentProfileDao = new ResidentProfileDao_Impl(this);
        }
        return _residentProfileDao;
      }
    }
  }

  @Override
  public PollDao pollDao() {
    if (_pollDao != null) {
      return _pollDao;
    } else {
      synchronized(this) {
        if(_pollDao == null) {
          _pollDao = new PollDao_Impl(this);
        }
        return _pollDao;
      }
    }
  }

  @Override
  public ScorecardDao scorecardDao() {
    if (_scorecardDao != null) {
      return _scorecardDao;
    } else {
      synchronized(this) {
        if(_scorecardDao == null) {
          _scorecardDao = new ScorecardDao_Impl(this);
        }
        return _scorecardDao;
      }
    }
  }

  @Override
  public ManifestoDao manifestoDao() {
    if (_manifestoDao != null) {
      return _manifestoDao;
    } else {
      synchronized(this) {
        if(_manifestoDao == null) {
          _manifestoDao = new ManifestoDao_Impl(this);
        }
        return _manifestoDao;
      }
    }
  }

  @Override
  public MetricDao metricDao() {
    if (_metricDao != null) {
      return _metricDao;
    } else {
      synchronized(this) {
        if(_metricDao == null) {
          _metricDao = new MetricDao_Impl(this);
        }
        return _metricDao;
      }
    }
  }

  @Override
  public PendingEventDao pendingEventDao() {
    if (_pendingEventDao != null) {
      return _pendingEventDao;
    } else {
      synchronized(this) {
        if(_pendingEventDao == null) {
          _pendingEventDao = new PendingEventDao_Impl(this);
        }
        return _pendingEventDao;
      }
    }
  }
}
