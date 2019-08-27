CREATE TABLE IF NOT EXISTS "Member" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "sno" varchar(16) NOT NULL UNIQUE, 
    "name" varchar(24) NOT NULL, 
    "phone" varchar(16) NULL, 
    "idcard" varchar(18) NULL, 
    "jointime" date NULL,
    "retired" bool NOT NULL DEFAULT 0,
    "male" bool NULL,
    "goodat" varchar(20) NULL,
    "retiretime" date NULL
);

CREATE TABLE IF NOT EXISTS "Activity" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "start_time" datetime NOT NULL, 
    "end_time" datetime NULL,
    "theme" text NOT NULL,
    "expense" integer NOT NULL, 
    "description" text NOT NULL
);

CREATE TABLE IF NOT EXISTS "ActivityParticipate" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "activity_id" integer NOT NULL REFERENCES "Activity" ("id") DEFERRABLE INITIALLY DEFERRED, 
    "member_id" integer NOT NULL REFERENCES "Member" ("id") DEFERRABLE INITIALLY DEFERRED,
    UNIQUE (activity_id, member_id)
);

CREATE TABLE IF NOT EXISTS "Competition" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "name" varchar(80) NOT NULL UNIQUE, 
    "start_time" datetime NULL,
    "end_time" datetime NULL,
    "form" varchar(10) NULL, 
    "description" text NULL,
    "url" text NULL,
    "rank" integer NULL,
    "score" integer NULL
);

CREATE TABLE IF NOT EXISTS "PersonalInvoice" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "expense" integer NOT NULL CHECK (expense > 0), 
    "refund" integer NOT NULL CHECK (expense >= 0), 
    "complete" bool NOT NULL DEFAULT 0, 
    "member_id" integer NOT NULL 
        REFERENCES "Member" ("id") DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE IF NOT EXISTS "CompetitionInvoice" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "amount" integer NOT NULL (expense > 0), 
    "complete" bool NOT NULL DEFAULT 0, 
    "competition_id" integer NOT NULL UNIQUE 
        REFERENCES "Competition" ("id") DEFERRABLE INITIALLY DEFERRED, 
    "principal_id" integer NULL 
        REFERENCES "Member" ("id") DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE IF NOT EXISTS "CompetitionBalance" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "record_time" datetime NOT NULL, 
    "is_income" bool NOT NULL, 
    "amount" integer NOT NULL CHECK (amount > 0), 
    "description" text NOT NULL, 
    "competition_id" integer NOT NULL 
        REFERENCES "Competition" ("id") DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE IF NOT EXISTS "Challenge" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "challenge" varchar(80) NOT NULL, 
    "score" real NULL CHECK (score > 0), 
    "competition_id" integer NOT NULL 
        REFERENCES "Competition" ("id") DEFERRABLE INITIALLY DEFERRED,
    UNIQUE (challenge, competition_id)
);

CREATE TABLE IF NOT EXISTS "ChallengeTag" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "tag" varchar(16) NOT NULL, 
    "challenge_id" integer NOT NULL 
        REFERENCES "Challenge" ("id") DEFERRABLE INITIALLY DEFERRED,
    UNIQUE (challenge_id, tag)
);

CREATE TABLE IF NOT EXISTS "ChallengeSolver" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
    "weight" real NOT NULL DEFAULT 1.0, 
    "challenge_id" integer NOT NULL 
        REFERENCES "Challenge" ("id") DEFERRABLE INITIALLY DEFERRED, 
    "member_id" integer NOT NULL 
        REFERENCES "Member" ("id") DEFERRABLE INITIALLY DEFERRED,
    UNIQUE (member_id, challenge_id)
);

CREATE UNIQUE INDEX "personalinvoice_member_id" ON "PersonalInvoice" ("member_id");
CREATE UNIQUE INDEX "competitioninvoice_principal_id" ON "CompetitionInvoice" ("principal_id");
CREATE UNIQUE INDEX "competitionbalance_competition_id" ON "CompetitionBalance" ("competition_id");
CREATE UNIQUE INDEX "challengetag_challenge_id" ON "ChallengeTag" ("challenge_id");
CREATE UNIQUE INDEX "challengesolver_challenge_id" ON "ChallengeSolver" ("challenge_id");
CREATE UNIQUE INDEX "challengesolver_member_id" ON "ChallengeSolver" ("member_id");
CREATE UNIQUE INDEX "challenge_competition_id" ON "Challenge" ("competition_id");
CREATE UNIQUE INDEX "activityparticipate_activity_id" ON "ActivityParticipate" ("activity_id");
CREATE UNIQUE INDEX "activityparticipate_member_id" ON "ActivityParticipate" ("member_id");
CREATE INDEX "member_name" ON "Member" ("name");
CREATE INDEX "competition_start_time" ON "Competition" ("start_time");

CREATE VIEW IF NOT EXISTS MemberActiveness
AS SELECT Member.id as member, Member.name, COUNT() as solve_num
FROM Member, ChallengeSolver
WHERE Member.id = ChallengeSolver.member_id 
GROUP BY Member.id ORDER BY Member.id;
