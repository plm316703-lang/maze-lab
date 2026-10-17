-- Step 4 : 재료(빛나는 원석, 심해 진주)는 등급이 없어서 inventory.grade 가 NULL 이어야 한다.
-- ddl-auto: update 는 이미 있는 칸의 NOT NULL 을 풀지 않으므로 직접 실행한다.
ALTER TABLE inventory MODIFY grade VARCHAR(5) NULL;
