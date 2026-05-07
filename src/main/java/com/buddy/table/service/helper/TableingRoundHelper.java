package com.buddy.table.service.helper;

import com.buddy.table.model.RoundAssignmentRow;
import com.buddy.table.model.TableAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TableingRoundHelper {

    // 1차시 테이블링
    public List<TableAssignment> runTableingRound1(
            Long sessionId,
            Long hostId,
            List<RoundAssignmentRow> participants
    ) {
        List<Integer> capacities = decideCapacities(participants.size());
        Map<Integer, Deque<RoundAssignmentRow>> bucket = session1BucketCreate(participants);

        List<TableAssignment> assignmentCreateList =
                tableingRound1(sessionId, hostId, participants, capacities, bucket);

        return assignmentCreateList;
    }

    // 1) 테이블 정원 결정 (MVP: 일단 전부 4인으로)
    public List<Integer> decideCapacities(int size) {
        List<Integer> capacities = new ArrayList<>();
        int q = size / 4;   // 4인 테이블 개수(기본)
        int r = size % 4;   // 남는 인원(0~3)

        int i = 0;
        while (i < q) {
            capacities.add(4);

            i += 1;
        }

        if (r == 0) return capacities;

        // r이 1~2면 마지막 테이블을 5 또는 6으로 키우는 방식(=남는 인원 흡수)
        if (r == 1 || r == 2) {
            int extraSize = r == 1 ? 5 : 6;

            if (capacities.isEmpty()) capacities.add(extraSize);
            capacities.set(capacities.size() - 1, extraSize);

            return capacities;
        }

        // r == 3, 4인 테이블 2개를 5인 + 6인으로 재조정
        if (r == 3 && capacities.size() >= 2) {
            capacities.set(capacities.size() - 1, 6);
            capacities.set(capacities.size() - 2, 5);

            return capacities;
        }

        // size가 5~7인데 r==3인 경우(=7명) 같은 특수 케이스
        // 운영상 이 케이스는 잘 없지만, 안전하게 7 한 테이블로 처리
        capacities.clear();
        capacities.add(size);

        return capacities;
    }

    // 2) 레벨 버킷 생성
    private Map<Integer, Deque<RoundAssignmentRow>> session1BucketCreate(
            List<RoundAssignmentRow> participants
    ) {
        Map<Integer, Deque<RoundAssignmentRow>> bucket = new HashMap<>();
        int lv = 5;
        while (lv > 0) {
            bucket.put(lv, new ArrayDeque<>());

            lv -= 1;
        }

        for (RoundAssignmentRow participant : participants) {
            bucket.computeIfAbsent(participant.level(), k -> new ArrayDeque<>())
                    .addLast(participant);
        }

        return bucket;
    }

    // 1차시 테이블링
    private List<TableAssignment> tableingRound1(
            Long sessionId,
            Long hostId,
            List<RoundAssignmentRow> participants,
            List<Integer> capacities, // 정원 체크 리스트
            Map<Integer, Deque<RoundAssignmentRow>> bucket
    ) {
        // 키 : 테이블 아이디, 밸류 : 테이블 참석자 정보
        Map<Integer, Deque<TableAssignment>> assignmentMap = new HashMap<>();

        int tableNo = 1;
        int filled = 0; // 테이블에 할당된 인원이 얼마나 채워 졌는지 판단 하는 변수
        int level = 5;

        while (capacities.size() >= tableNo) {
            int capacity = capacities.get(tableNo - 1);

            RoundAssignmentRow row = bucket.get(level).pollFirst();

            if (row == null) {
                level -= 1;

                if (level < 1) break;
                continue;
            }

            // 현재까지 배치된 테이블 불러옴
            Deque<TableAssignment> currentAssignTable =
                    assignmentMap.getOrDefault(tableNo, new ArrayDeque<>());
            // 위에 디큐 테이블에 현재 뽑아온 타겟 원소 삽입
            currentAssignTable.addLast(getAssignmentRow(sessionId, row, tableNo, 1));
            // 맵에 추가
            assignmentMap.put(tableNo, currentAssignTable);

            filled += 1;
            if (filled == capacity) {
                tableNo += 1;
                filled = 0;
                level = 5; // 최대한 높은 레벨부터 채우기
            }

        }

        Map<Long, Integer> levelMap = buildUserLevelMap(participants);
        // Round1 테이블링에서 테이블별 상태를 추적하기 위한 집계(통계) 레코드
        Map<Integer, RoundTableSchema> round1TableInfoMap = buildRoundTableInfoMap(assignmentMap, levelMap);

        // 성비 리밸런싱
        // 원리 :
        // 여성이 과반수 이상 차지하는 테이블에서, 한명을 선택 해서
        // 남성이 극단적으로 많은 테이블에서의 한명과 교환
        // 이때 교환되는 원소는 Round2TableSchema 에 있는 평균 레벨 기준으로 선택 된다.
        rebalanceGender(participants, assignmentMap, round1TableInfoMap);

        // 리더 테이블 1로 배정
        rebalanceLeaderTable(hostId, assignmentMap, round1TableInfoMap, levelMap);

        return getTableAssignmentList(assignmentMap);
    }

    // 2차시 테이블링
    public List<TableAssignment> runTableingRound2(
            Long sessionId,
            Long hostId,
            List<RoundAssignmentRow> participants,
            List<TableAssignment> assignmentTable1List
    ) {
        // capacities = 테이블 정원 결정
        List<Integer> capacities = decideCapacities(assignmentTable1List.size());
        // bucket = 키값 : 1차시 테이블 아이디, 밸류 값 : 1차시 참석자 리스트
        Map<Integer, Deque<RoundAssignmentRow>> bucket =
                session2BucketCreate(participants, assignmentTable1List);

        // 실제 테이블 배치 실행
        List<TableAssignment> assignmentCreateList =
                tableingRound2(sessionId, hostId, participants, assignmentTable1List, capacities, bucket);

        return assignmentCreateList;
    }

    // 1차시 조건 가지고, 해시맵 만들어서 리턴
    // 키값 : 1차시 테이블 아이디
    // 밸류 값 : 1차시 참석자 리스트
    private Map<Integer, Deque<RoundAssignmentRow>> session2BucketCreate(
            List<RoundAssignmentRow> participants, // 전체 참석자
            List<TableAssignment> session1AssignmentList // 세션1 테이블 배치 결과 로우
    ) {
        // 리턴할 해쉬맵
        Map<Integer, Deque<RoundAssignmentRow>> bucket = new HashMap<>();
        // participants 토대로,
        // 키 : 유저 아이디, 밸류 : 참석자 정보(RoundAssignmentRow) 매핑 하는 해쉬맵
        Map<Long, RoundAssignmentRow> byUserId = new HashMap<>();

        // byUserId 맵 생성
        for (RoundAssignmentRow user : participants) byUserId.put(user.userId(), user);

        // 세션1 테이블 배치 결과 로우 루핑돌면서, bucket 생성
        for (TableAssignment info : session1AssignmentList) {
            RoundAssignmentRow user = byUserId.get(info.userId());

            if (user != null) {
                RoundAssignmentRow row = new RoundAssignmentRow(
                        info.sessionId(),
                        info.userId(),
                        info.tableNo(),
                        user.gender(),
                        user.level()
                );

                // bucket에 user가 속했던 세션1 테이블 있으면 맨뒤에 추가, 없으면 빈 디큐 생성
                bucket.computeIfAbsent(row.s1GroupId(), k -> new ArrayDeque<>())
                        .addLast(row);
            }
        }

        return bucket;
    }

    // 실제 자리 배치 진행
    // 테이블 2는 "라운드 로빈" 알고리즘으로 진행
    private List<TableAssignment> tableingRound2(
            Long sessionId,
            Long hostId,
            List<RoundAssignmentRow> participants,
            List<TableAssignment> assignmentTable1List,
            List<Integer> capacities, // 정원 체크 리스트
            Map<Integer, Deque<RoundAssignmentRow>> bucket // 키 : 1차시 테이블 아이디, 밸류 : 1차시 테이블 참석자 정보
    ) {

        // 실제로 2차시 테이블링 결과 담을 해쉬맵
        // 키 : 테이블 아이디, 밸류 : 테이블 참석자 정보
        Map<Integer, Deque<TableAssignment>> assignmentMap = new HashMap<>();
        // Round2 테이블링에서 테이블별 상태를 추적하기 위한 집계(통계) 레코드
        Map<Integer, RoundTableSchema> round2TableInfoMap = new HashMap<>();
        // 메인 루프에서 테이블을 찾지 못할 경우, 무한루프에 빠질가능성이 있으므로, 아래 큐에 담아서 관리
        Queue<RoundAssignmentRow> pendingQueue = new ArrayDeque<>();

        int tableNo = 1;
        int remaining = assignmentTable1List.size(); // 남은 사람 체크

        while (remaining > 0) {

            // 현재 까지 배치 중인 2차 테이블 추출
            Deque<TableAssignment> currentAssignTable =
                    assignmentMap.getOrDefault(tableNo, new ArrayDeque<>());

            // 1차시 테이블에서 추출
            Deque<RoundAssignmentRow> roundAssignmentRows = bucket.get(tableNo);
            if (roundAssignmentRows == null) throw new IllegalArgumentException("추출 데이터 이상");
            // 테이블에서 유저 데이터 폴링
            RoundAssignmentRow polled = roundAssignmentRows.pollFirst();
            if (polled == null) {
                tableNo = (tableNo < capacities.size()) ? tableNo + 1 : 1;

                continue;
            }

            // 상태를 추적하기 위한 집계(통계) 레코드 생성 및 추출
            RoundTableSchema info =
                    round2TableInfoMap.getOrDefault(tableNo, RoundTableSchema.basicModel(tableNo));

            // 테이블 2에서 자리 배치시 사전 조건을 만족 하는지 체크
            // 중복 : 이전 테이블 인원 겹침 2인 이하
            // 레벨 : 최고 레벨과 최저 레벨 2인 이하
            Evaluator evaluator = currentAssignTable.isEmpty()
                    // currentAssignTable이 비었을 경우 -> 최초 시도일 경우 항상 OK
                    ? Evaluator.OK
                    // currentAssignTable의 크기가 할당된 테이블 크기 보다 클 경우 항상 NO
                    : currentAssignTable.size() >= capacities.get(tableNo - 1)
                    ? Evaluator.NO
                    : evaluateAssign(polled, currentAssignTable, info);

            // evaluator가 BAD일 경우, 최선의 테이블 배치를 기억하기 위한 레코드
            BadCaseForSession2 badCaseForSession2 = BadCaseForSession2.basicModel(tableNo);

            // Evaluator가 BAD일 경우, badCaseForSession2값 업데이트
            if (evaluator.equals(Evaluator.BAD)) {
                int levelDifference = info.maxLevel - polled.level();

                badCaseForSession2 = badCaseForSession2.withBadCase(
                        true,
                        polled.userId(),
                        tableNo,
                        getNewLevelDifference(badCaseForSession2.levelDifference, levelDifference)
                );
            }

            // evaluator가 BAD, NO일 경우, 추가로 루프 태움,
            // 정상 케이스가 있으면 해당 테이블에 삽입
            // 테이블 갯수만큼 돌았음에도 찾지 못할 경우, 대기 큐 삽입
            if (evaluator.equals(Evaluator.BAD) || evaluator.equals(Evaluator.NO)) {
                int tries = 0;
                // 시작점은 다음 테이블 부터
                int tableCount = capacities.size();
                int loopStart = (tableNo < tableCount) ? tableNo + 1 : 1;

                while (tries < tableCount) {
                    // 루프 돌면서 판독할 대상 테이블 추출
                    info = round2TableInfoMap.getOrDefault(loopStart, RoundTableSchema.basicModel(loopStart));
                    currentAssignTable =
                            assignmentMap.getOrDefault(loopStart, new ArrayDeque<>());

                    // 재판독
                    Evaluator reEvaluator =
                            currentAssignTable.size() >= capacities.get(loopStart - 1)
                                    ? Evaluator.NO
                                    : evaluateAssign(polled, currentAssignTable, info);

                    // 정상일 경우, 루프 종료
                    if (reEvaluator.equals(Evaluator.OK)) {
                        evaluator = reEvaluator;
                        tableNo = loopStart;

                        break;
                    }

                    // BAD일 경우, badCaseForSession2 값을 필요시 업데이트
                    // 필요 기준 -> 영어 레벨 차이 (가장 높은 사람, 가장 낮은 사람)
                    // 지금 BAD case의 레벨 차가 이전 BAD case보다 작을 경우 badCaseForSession2 재할당
                    if (reEvaluator.equals(Evaluator.BAD)) {
                        int levelDifference = info.maxLevel - polled.level();

                        badCaseForSession2 = badCaseForSession2.withBadCase(
                                true,
                                polled.userId(),
                                loopStart,
                                getNewLevelDifference(badCaseForSession2.levelDifference, levelDifference)
                        );
                    }

                    loopStart = (loopStart < tableCount) ? loopStart + 1 : 1;
                    tries += 1;

                    // BAD 케이스일 경우, 최선의 테이블의 데이터 추가
                    if (badCaseForSession2.isBadCase) {
                        evaluator = reEvaluator;

                        break;
                    }

                }

                // 내부 와일 루프 (BAD, NO 케이스 판독) 종료 이후 tableNo 업데이트
                tableNo = loopStart;

            }

            if (evaluator.equals(Evaluator.OK) || badCaseForSession2.isBadCase) {
                // 정상(OK) 또는 차선(BAD 후보)일 때 실제 배치 테이블 결정
                int targetTable = badCaseForSession2.isBadCase
                        ? badCaseForSession2.tableNo()
                        : tableNo;

                // 삽입 + 통계 갱신은 헬퍼로 통일
                addToRound2Table(sessionId, polled, targetTable, assignmentMap, round2TableInfoMap);

            } else {
                // 결국 NO 일 경우, 대기 큐의 삽입
                // 이후 데이터 처리
                pendingQueue.add(polled);
            }

            tableNo = (tableNo < capacities.size()) ? tableNo + 1 : 1;

            // 테이블에서 폴링 한 원소가, 삽입 될 때마다, 1씩 감소
            remaining -= 1;
        }

        // 대기큐가 비었을 경우, 함수 종료
        if (pendingQueue.isEmpty()) return getTableAssignmentList(assignmentMap);

        // 이미 위에 라운드로빈으로 정원이 꽉찬 테이블은 대기 큐 삽입 타겟 테이블에서 제거
        List<Integer> tableNos = new ArrayList<>();
        int t = 1;
        while (t <= capacities.size()) {
            tableNos.add(t);

            t += 1;
        }

        // 리밸런싱 작업
        // 리밸런싱은 1차시 중복 인원 보단 레벨 비슷한 사람 배치에 집중
        while (pendingQueue.isEmpty() == false) {
            // 대기 큐에서 한 명 추출
            RoundAssignmentRow row = pendingQueue.poll();

            // 최선의 테이블 번호 탐색
            // 첫번째 케이스
            // 엄격 -> 중복 2인, 레벨 차이 3 이하
            int bestTableNo = findBestTableNo(
                    tableNos,
                    capacities,
                    assignmentMap,
                    round2TableInfoMap,
                    row,
                    true
            );

            // 첫번째 케이스에서 -1이 리턴된 경우, 최선의 테이블 번호 탐색
            // 두번째 케이스
            // 엄격 -> 중복 3인, 레벨 차이 4 이하
            if (bestTableNo == -1) {
                bestTableNo = findBestTableNo(
                        tableNos,
                        capacities,
                        assignmentMap,
                        round2TableInfoMap,
                        row,
                        false
                );
            }

            // 최종적으로 예외 상태 체크
            if (bestTableNo == -1) {
                throw new IllegalStateException(
                        "리밸런싱 실패. " +
                                "userId=" + row.userId() +
                                ", level=" + row.level() +
                                ", s1GroupId=" + row.s1GroupId()
                );
            }

            // 마지막으로 테이블링 결과를 맵에 추가
            addToRound2Table(sessionId, row, bestTableNo, assignmentMap, round2TableInfoMap);

        }

        // 성비 리밸런싱
        // 원리 :
        // 여성이 과반수 이상 차지하는 테이블에서, 한명을 선택 해서
        // 남성이 극단적으로 많은 테이블에서의 한명과 교환
        // 이때 교환되는 원소는 Round2TableSchema 에 있는 평균 레벨 기준으로 선택 된다.
        rebalanceGender(participants, assignmentMap, round2TableInfoMap);

        Map<Long, Integer> levelMap = buildUserLevelMap(participants);
        round2TableInfoMap = buildRoundTableInfoMap(assignmentMap, levelMap);

        // 리더 테이블 1로 배정
        rebalanceLeaderTable(hostId, assignmentMap, round2TableInfoMap, levelMap);

        // assignmentMap 에 밸류인 디큐들을, 리스트 형식으로 바꿔서 리턴
        // flatMap이 내부에서 디큐들을 쭉 펼져처 합쳐줌.
        return getTableAssignmentList(assignmentMap);

    }

    private List<TableAssignment> getTableAssignmentList(Map<Integer, Deque<TableAssignment>> assignmentMap) {
        return assignmentMap.values().stream()
                .flatMap(Deque::stream)
                .toList();
    }

    // 대기큐에서 원소 뽑아서 최선의 테이블 탐색 메소드
    private int findBestTableNo(
            List<Integer> tableNos,
            List<Integer> capacities,
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Integer, RoundTableSchema> round2TableInfoMap,
            RoundAssignmentRow row,
            boolean firstCheckCase
    ) {
        double bestDiff = Double.MAX_VALUE;
        int bestTableNo = -1;

        for (Integer tNo : tableNos) {
            RoundTableSchema schema =
                    round2TableInfoMap.getOrDefault(tNo, RoundTableSchema.basicModel(tNo));

            // 1) 정원 체크
            if (schema.tableSize().equals(capacities.get(tNo - 1))) continue;

            // 2) 겹침 하드 가드
            long count = getCountFromS1Table(assignmentMap.get(tNo), row.s1GroupId());
            if (count >= (firstCheckCase ? 2 : 3)) continue;

            // 3) 레벨 range 가드(옵션)
            int newMax = Math.max(schema.maxLevel, row.level());
            int newMin = Math.min(schema.minLevel, row.level());
            if (newMax - newMin > (firstCheckCase ? 3 : 4)) continue;

            // 4) avg diff 최소
            double diff = Math.abs(row.level() - schema.getAvg());
            if (bestDiff > diff) {
                bestDiff = diff;
                bestTableNo = tNo;
            }

        }

        return bestTableNo;

    }

    // 테이블 2에서 자리 배치시 사전 조건을 만족 하는지 체크
    // 중복 : 이전 테이블 인원 겹침 2인 이하
    // 레벨 : 최고 레벨과 최저 레벨 2인 이하
    //  •	OK : 중복 OK + 레벨 OK
    //	•	BAD : 중복 OK + 레벨만 별로
    //	•	NO : 중복 때문에 불가(2)
    private Evaluator evaluateAssign(
            RoundAssignmentRow polled,
            Deque<TableAssignment> table,
            RoundTableSchema info
    ) {

        // 1차시 그룹 아이디 추출
        Integer groupId = polled.s1GroupId();
        if (groupId == null) throw new IllegalArgumentException("그룹 아이디 이상");

        // 타겟 테이블의 이전 차시를 같이한 사람 수 측정
        long count = getCountFromS1Table(table, groupId);

        // 중복 때문에 불가(2)
        if (count >= 2) return Evaluator.NO;

        int newMaxLevel = Math.max(info.maxLevel, polled.level());
        int newMinLevel = Math.min(info.minLevel, polled.level());
        // 레벨 차이는 범위로 계산
        int newRange = newMaxLevel - newMinLevel;

        // OK : 중복 OK + 레벨 OK
        if (newRange <= 2) return Evaluator.OK;
        // 중복 OK + 레벨만 별로
        if (newRange <= 3) return Evaluator.BAD;

        return Evaluator.NO;

    }

    // 현재까지 배치 결과중 중복되는 인원 수 체크
    private long getCountFromS1Table(Deque<TableAssignment> table, Integer groupId) {
        return table.stream()
                .filter(i -> Objects.equals(i.s1GroupId(), groupId))
                .count();
    }

    // 재평가 경우도 Evaluator.BAD 일 경우, 최선의 선택지 수정 필요 여부를 판단 위한 메소드.
    private int getNewLevelDifference(Integer target, int levelDifference) {
        // 현재 타겟의 levelDifference이
        // 전 최선의 테이블 levelDifference 보다 작을 경우 값 업데이트
        return (target == null)
                ? levelDifference
                : Math.min(target, levelDifference);

    }

    // 라운드2에 실제 테이블 데이터 삽입 + 테이블 통계(레벨/평균) 갱신을 한 번에 처리하는 헬퍼 메소드
    private void addToRound2Table(
            Long sessionId,
            RoundAssignmentRow row, // 배치할 대상(유저/레벨/그룹정보 포함)
            int targetTableNo,           // 배치할 2차 테이블 번호
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Integer, RoundTableSchema> round2TableInfoMap
    ) {
        // 1) targetTableNo에 해당하는 현재 테이블(Deque) 조회
        Deque<TableAssignment> table =
                assignmentMap.getOrDefault(targetTableNo, new ArrayDeque<>());
        // 2) 실제 배치 row 생성 후 테이블에 추가
        table.addLast(getAssignmentRow(sessionId, row, targetTableNo, 2));
        // 3) 갱신된 테이블을 다시 map에 저장
        assignmentMap.put(targetTableNo, table);

        // 4) 테이블 상태(통계) 조회
        RoundTableSchema prev = round2TableInfoMap.getOrDefault(targetTableNo,
                RoundTableSchema.basicModel(targetTableNo)
        );

        // 5) 테이블 상태 갱신 (max/min/합계/인원수)
        int level = row.level();
        int newMax = Math.max(prev.maxLevel(), level);
        int newMin = Math.min(prev.minLevel(), level);

        round2TableInfoMap.put(targetTableNo,
                new RoundTableSchema(
                        targetTableNo,
                        prev.tableSize() + 1,
                        newMax,
                        newMin,
                        prev.levelSum() + level
                )
        );

    }

    // 0) 성비 리밸런싱 전체 수행
    private void rebalanceGender(
            List<RoundAssignmentRow> participants,
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Integer, RoundTableSchema> tableInfoMap
    ) {
        List<DonorTableInfo> femaleDonorList = new ArrayList<>();
        List<DonorTableInfo> maleDonorList = new ArrayList<>();

        collectDonorTables(participants, assignmentMap, femaleDonorList, maleDonorList);

        if (maleDonorList.isEmpty() || femaleDonorList.isEmpty()) return;

        Map<Long, Integer> userLevelMap = buildUserLevelMap(participants);

        SwapPair swapPair = findSwapPair(
                femaleDonorList,
                maleDonorList,
                tableInfoMap,
                userLevelMap
        );

        if (swapPair == null) return;

        applySwap(assignmentMap, swapPair);
    }

    // 1) 성별리밸런싱 위해, 대상 테이블인지 판독
    // 테이블에 여성이 과반수 이상일 경우, 여성 이동
    // 대상은 테이블에 전원이 남성인 경우
    private DonorTableInfo getDonorInfo(
            List<RoundAssignmentRow> participants,
            Deque<TableAssignment> targetTable
    ) {
        if (targetTable == null) throw new IllegalArgumentException("");
        Map<Long, String> userInfoMap = new HashMap<>();
        List<Long> femaleDonorIdList = new ArrayList<>();
        List<Long> maleDonorIdList = new ArrayList<>();

        for (RoundAssignmentRow participant : participants) {
            userInfoMap.put(participant.userId(), participant.gender());
        }

        Integer tableNo = null;
        int tableSize = targetTable.size();
        int femaleCnt = 0;
        for (TableAssignment member : targetTable) {
            tableNo = tableNo == null ? member.tableNo() : tableNo;

            if (userInfoMap.get(member.userId()).equals("F")) {
                femaleDonorIdList.add(member.userId());
                femaleCnt += 1;

            } else maleDonorIdList.add(member.userId());
        }

        int targetDonorSetter = 0; // 0 : 타겟X, 1 : 남성 도너, 2: 여성 도너

        switch (tableSize) {
            case 4:
                if (femaleCnt <= 1) targetDonorSetter = 1;
                if (femaleCnt == 4) targetDonorSetter = 2;
                break;
            case 5:
                if (femaleCnt <= 1) targetDonorSetter = 1;
                if (femaleCnt == 4 || femaleCnt == 5) targetDonorSetter = 2;
                break;
            case 6:
                if (femaleCnt <= 2) targetDonorSetter = 1;
                if (femaleCnt == 4 || femaleCnt == 5 || femaleCnt == 6) targetDonorSetter = 2;
                break;
        }

        return new DonorTableInfo(
                tableNo,
                tableSize,
                femaleCnt,
                targetDonorSetter,
                targetDonorSetter == 1
                        ? maleDonorIdList
                        : targetDonorSetter == 2
                        ? femaleDonorIdList
                        : new ArrayList<>()
        );

    }

    private void collectDonorTables(
            List<RoundAssignmentRow> participants,
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            List<DonorTableInfo> femaleDonorList,
            List<DonorTableInfo> maleDonorList
    ) {
        for (Deque<TableAssignment> targetTable : assignmentMap.values()) {
            DonorTableInfo donorTableInfo = getDonorInfo(participants, targetTable);

            if (donorTableInfo.targetDonorSetter == 0) continue;

            if (donorTableInfo.targetDonorSetter == 1) maleDonorList.add(donorTableInfo);
            else femaleDonorList.add(donorTableInfo);
        }

        maleDonorList.sort(Comparator.comparingInt((DonorTableInfo a) -> a.femaleCnt)
                .thenComparingInt(a -> a.tableSize)
        );

        femaleDonorList.sort((a, b) -> Integer.compare(
                getFemaleDonorPriority(b),
                getFemaleDonorPriority(a)
        ));

    }

    private int getFemaleDonorPriority(DonorTableInfo info) {
        if (info.tableSize == 4 && info.femaleCnt == 4) return 100;
        if (info.tableSize == 5 && info.femaleCnt == 5) return 90;
        if (info.tableSize == 5 && info.femaleCnt == 4) return 80;
        if (info.tableSize == 6 && info.femaleCnt == 6) return 70;
        if (info.tableSize == 6 && info.femaleCnt == 5) return 60;
        if (info.tableSize == 6 && info.femaleCnt == 4) return 50;

        return 0;
    }

    // 2) 테이블에 유저 정보 추가
    private TableAssignment getAssignmentRow(
            Long sessionId,
            RoundAssignmentRow row,
            int tableNo,
            int roundNo
    ) {

        return new TableAssignment(
                sessionId,
                row.userId(),
                roundNo,
                tableNo,
                roundNo == 1 ? null : row.s1GroupId(),
                LocalDateTime.now()
        );

    }

    // 3) 유저 레벨 맵 생성 메소드
    private Map<Long, Integer> buildUserLevelMap(List<RoundAssignmentRow> participants) {
        Map<Long, Integer> userLevelMap = new HashMap<>();

        for (RoundAssignmentRow participant : participants) {
            userLevelMap.put(participant.userId(), participant.level());
        }

        return userLevelMap;
    }

    // 4) swap 대상 찾기 메소드
    private SwapPair findSwapPair(
            List<DonorTableInfo> femaleDonorList,
            List<DonorTableInfo> maleDonorList,
            Map<Integer, RoundTableSchema> tableInfoMap,
            Map<Long, Integer> userLevelMap
    ) {
        for (DonorTableInfo femaleList : femaleDonorList) {
            for (DonorTableInfo maleList : maleDonorList) {

                RoundTableSchema femaleSchema = tableInfoMap.get(femaleList.tableNo);
                RoundTableSchema maleSchema = tableInfoMap.get(maleList.tableNo);

                if (femaleSchema == null || maleSchema == null) continue;

                SwapPair sameFloorPair = findSwapPairBySameFloorAvg(
                        femaleList,
                        maleList,
                        femaleSchema,
                        maleSchema,
                        userLevelMap
                );
                if (sameFloorPair != null) return sameFloorPair;

                SwapPair avgDiffPair = findSwapPairByAvgComparison(
                        femaleList,
                        maleList,
                        femaleSchema,
                        maleSchema,
                        userLevelMap
                );
                if (avgDiffPair != null) return avgDiffPair;

            }

        }

        return null;
    }

    // 5) 평균 버림값 같을 때 찾기
    private SwapPair findSwapPairBySameFloorAvg(
            DonorTableInfo femaleList,
            DonorTableInfo maleList,
            RoundTableSchema femaleSchema,
            RoundTableSchema maleSchema,
            Map<Long, Integer> userLevelMap
    ) {
        double femaleAvg = femaleSchema.getAvg();
        double maleAvg = maleSchema.getAvg();

        if (Math.floor(femaleAvg) != Math.floor(maleAvg)) return null;

        for (Long femaleId : femaleList.candidateUserIdList) {
            for (Long maleId : maleList.candidateUserIdList) {
                Integer femaleLv = userLevelMap.get(femaleId);
                Integer maleLv = userLevelMap.get(maleId);

                if (femaleLv != null && femaleLv.equals(maleLv)) {
                    return new SwapPair(
                            femaleId, femaleSchema.tableNo,
                            maleId, maleSchema.tableNo
                    );

                }

            }

        }

        return null;
    }

    // 6) 평균 차이 날 때 찾기
    private SwapPair findSwapPairByAvgComparison(
            DonorTableInfo femaleList,
            DonorTableInfo maleList,
            RoundTableSchema femaleSchema,
            RoundTableSchema maleSchema,
            Map<Long, Integer> userLevelMap
    ) {
        double femaleAvg = femaleSchema.getAvg();
        double maleAvg = maleSchema.getAvg();

        if (femaleAvg > maleAvg) {
            Long femaleId = findUserIdByLevel(
                    femaleList.candidateUserIdList,
                    userLevelMap,
                    femaleSchema.minLevel()
            );

            Long maleId = findUserIdByLevel(
                    maleList.candidateUserIdList,
                    userLevelMap,
                    maleSchema.maxLevel()
            );

            if (femaleId != null && maleId != null) {
                return new SwapPair(
                        femaleId, femaleSchema.tableNo,
                        maleId, maleSchema.tableNo
                );
            }
        }

        if (maleAvg > femaleAvg) {
            Long femaleId = findUserIdByLevel(
                    femaleList.candidateUserIdList,
                    userLevelMap,
                    femaleSchema.maxLevel()
            );

            Long maleId = findUserIdByLevel(
                    maleList.candidateUserIdList,
                    userLevelMap,
                    maleSchema.minLevel()
            );

            if (femaleId != null && maleId != null) {
                return new SwapPair(
                        femaleId, femaleSchema.tableNo,
                        maleId, maleSchema.tableNo
                );
            }
        }

        return null;
    }

    // 7) 특정 레벨 유저 찾기
    private Long findUserIdByLevel(
            List<Long> candidateUserIdList,
            Map<Long, Integer> userLevelMap,
            int targetLevel
    ) {
        for (Long userId : candidateUserIdList) {
            Integer level = userLevelMap.get(userId);

            if (level != null && level.equals(targetLevel)) return userId;
        }

        return null;
    }

    // 8) 실제 swap 반영 메소드
    private void applySwap(
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            SwapPair swapPair
    ) {
        Deque<TableAssignment> femaleTableAssign = assignmentMap.get(swapPair.femaleTableNo());
        Deque<TableAssignment> maleTableAssign = assignmentMap.get(swapPair.maleTableNo());

        if (femaleTableAssign == null || maleTableAssign == null) return;

        TableAssignment femalePolled = extractAndChangeTable(
                femaleTableAssign,
                swapPair.femaleUserId(),
                swapPair.maleTableNo()
        );

        TableAssignment malePolled = extractAndChangeTable(
                maleTableAssign,
                swapPair.maleUserId(),
                swapPair.femaleTableNo()
        );

        if (femalePolled == null || malePolled == null) return;

        femaleTableAssign.addLast(malePolled);
        maleTableAssign.addLast(femalePolled);

        assignmentMap.put(swapPair.femaleTableNo(), femaleTableAssign);
        assignmentMap.put(swapPair.maleTableNo(), maleTableAssign);
    }

    // 9) deque에서 꺼내는 공통 메소드
    private TableAssignment extractAndChangeTable(
            Deque<TableAssignment> tableAssign,
            Long targetUserId,
            int newTableNo
    ) {
        int size = tableAssign.size();

        while (size > 0) {
            TableAssignment poll = tableAssign.poll();

            if (poll == null) return null;

            if (poll.userId().equals(targetUserId))
                return poll.updateTableAssignment(newTableNo);

            tableAssign.addLast(poll);

            size--;
        }

        return null;
    }

    // 리더 테이블 번호 찾기
    private int findLeaderTableNo(
            Long hostId,
            Map<Integer, Deque<TableAssignment>> assignmentMap
    ) {
        for (Integer tableNo : assignmentMap.keySet()) {
            Deque<TableAssignment> table = assignmentMap.get(tableNo);
            if (table == null) {
                continue;
            }

            for (TableAssignment member : table) {
                if (member.userId().equals(hostId)) {
                    return tableNo;
                }
            }
        }

        return -1;
    }

    // 4인일 때 전체 스왑
    private void swapWholeTableWithTableOne(
            int leaderTableNo,
            Deque<TableAssignment> leaderTable,
            Deque<TableAssignment> targetTable,
            Map<Integer, Deque<TableAssignment>> assignmentMap
    ) {
        Deque<TableAssignment> newLeaderTable = new ArrayDeque<>();
        Deque<TableAssignment> newTargetTable = new ArrayDeque<>();

        while (leaderTable.isEmpty() == false) {
            TableAssignment member = leaderTable.pollFirst();
            if (member != null) {
                newTargetTable.addLast(member.updateTableAssignment(1));
            }
        }

        while (targetTable.isEmpty() == false) {
            TableAssignment member = targetTable.pollFirst();
            if (member != null) {
                newLeaderTable.addLast(member.updateTableAssignment(leaderTableNo));
            }
        }

        assignmentMap.put(1, newTargetTable);
        assignmentMap.put(leaderTableNo, newLeaderTable);
    }

    // 5/6인일 때 리더만 1:1 스왑
    private void swapLeaderOnlyWithTableOne(
            Long hostId,
            int leaderTableNo,
            Deque<TableAssignment> leaderTable,
            Deque<TableAssignment> targetTable,
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Integer, RoundTableSchema> roundTableInfoMap,
            Map<Long, Integer> levelMap
    ) {
        RoundTableSchema leaderSchema = roundTableInfoMap.get(leaderTableNo);
        RoundTableSchema targetSchema = roundTableInfoMap.get(1);

        if (leaderSchema == null || targetSchema == null) {
            throw new IllegalArgumentException("리더 테이블 또는 1조 통계 정보가 없습니다.");
        }

        TableAssignment leaderMember = null;
        for (TableAssignment member : leaderTable) {
            if (member.userId().equals(hostId)) {
                leaderMember = member;
                break;
            }
        }

        if (leaderMember == null) {
            throw new IllegalArgumentException("리더 멤버를 찾지 못했습니다.");
        }

        TableAssignment targetSwapMember = pickSwapTargetFromTableOne(
                leaderMember,
                targetTable,
                leaderSchema,
                targetSchema,
                levelMap
        );

        if (targetSwapMember == null) {
            throw new IllegalArgumentException("1조에서 리더와 스왑할 대상을 찾지 못했습니다.");
        }

        leaderTable.remove(leaderMember);
        targetTable.remove(targetSwapMember);

        leaderTable.addLast(targetSwapMember.updateTableAssignment(leaderTableNo));
        targetTable.addLast(leaderMember.updateTableAssignment(1));

        assignmentMap.put(leaderTableNo, leaderTable);
        assignmentMap.put(1, targetTable);
    }

    // 1조에서 스왑 대상 고르기
    private TableAssignment pickSwapTargetFromTableOne(
            TableAssignment leaderMember,
            Deque<TableAssignment> targetTable,
            RoundTableSchema leaderSchema,
            RoundTableSchema targetSchema,
            Map<Long, Integer> levelMap
    ) {
        double leaderAvg = leaderSchema.getAvg();
        double targetAvg = targetSchema.getAvg();

        Integer leaderLevel = levelMap.get(leaderMember.userId());
        if (leaderLevel == null) {
            throw new IllegalArgumentException("리더 레벨 정보가 없습니다.");
        }

        if (Math.floor(leaderAvg) == Math.floor(targetAvg)) {
            for (TableAssignment member : targetTable) {
                Integer memberLevel = levelMap.get(member.userId());
                if (memberLevel != null && memberLevel.equals(leaderLevel)) {
                    return member;
                }
            }
        }

        if (leaderAvg > targetAvg) {
            TableAssignment result = null;
            int maxLevel = -1;

            for (TableAssignment member : targetTable) {
                Integer memberLevel = levelMap.get(member.userId());
                if (memberLevel != null && memberLevel > maxLevel) {
                    maxLevel = memberLevel;
                    result = member;
                }
            }

            return result;
        }

        if (targetAvg > leaderAvg) {
            TableAssignment result = null;
            int minLevel = Integer.MAX_VALUE;

            for (TableAssignment member : targetTable) {
                Integer memberLevel = levelMap.get(member.userId());
                if (memberLevel != null && minLevel > memberLevel) {
                    minLevel = memberLevel;
                    result = member;
                }
            }

            return result;
        }

        TableAssignment result = null;
        int minDiff = Integer.MAX_VALUE;

        for (TableAssignment member : targetTable) {
            Integer memberLevel = levelMap.get(member.userId());
            if (memberLevel != null) {
                int diff = Math.abs(leaderLevel - memberLevel);
                if (minDiff > diff) {
                    minDiff = diff;
                    result = member;
                }
            }
        }

        return result;
    }

    //  리더 1조 배정 메서드
    private void rebalanceLeaderTable(
            Long hostId,
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Integer, RoundTableSchema> roundTableInfoMap,
            Map<Long, Integer> levelMap
    ) {
        int leaderTableNo = findLeaderTableNo(hostId, assignmentMap);
        if (leaderTableNo == -1) {
            throw new IllegalArgumentException("리더가 배치된 테이블을 찾지 못했습니다.");
        }

        if (leaderTableNo == 1) {
            return;
        }

        Deque<TableAssignment> leaderTable = assignmentMap.get(leaderTableNo);
        Deque<TableAssignment> targetTable = assignmentMap.get(1);

        if (leaderTable == null || targetTable == null) {
            throw new IllegalArgumentException("리더 테이블 또는 1조가 존재하지 않습니다.");
        }

        if (leaderTable.size() == 4) {
            swapWholeTableWithTableOne(leaderTableNo, leaderTable, targetTable, assignmentMap);
            return;
        }

        swapLeaderOnlyWithTableOne(
                hostId,
                leaderTableNo,
                leaderTable,
                targetTable,
                assignmentMap,
                roundTableInfoMap,
                levelMap
        );
    }

    // 라운드 1 RoundTableSchema 생성
    private Map<Integer, RoundTableSchema> buildRoundTableInfoMap(
            Map<Integer, Deque<TableAssignment>> assignmentMap,
            Map<Long, Integer> levelMap
    ) {
        Map<Integer, RoundTableSchema> roundTableInfoMap = new HashMap<>();

        for (Integer key : assignmentMap.keySet()) {
            Deque<TableAssignment> tableAssignments = assignmentMap.get(key);
            if (tableAssignments == null || tableAssignments.isEmpty()) {
                continue;
            }

            int max = -1;
            int min = -1;
            int levelSum = 0;

            for (TableAssignment table : tableAssignments) {
                Integer userLevel = levelMap.get(table.userId());
                if (userLevel == null) {
                    continue;
                }

                levelSum += userLevel;
                max = userLevel > max ? userLevel : max;
                min = min == -1 ? userLevel : (min > userLevel ? userLevel : min);
            }

            roundTableInfoMap.put(
                    key,
                    new RoundTableSchema(
                            key,
                            tableAssignments.size(),
                            max,
                            min,
                            levelSum
                    )
            );
        }

        return roundTableInfoMap;
    }

    // Round2 배치에서 완벽한 OK 자리가 없을 때를 대비해
    // BAD 후보(=넣을 수는 있지만 레벨 조건이 아쉬운 자리) 중 최선의 선택지를 기억하는 레코드.
    private static record BadCaseForSession2(
            Boolean isBadCase,
            Long userId,
            Integer tableNo,
            Integer levelDifference
    ) {

        // 기본값
        private static BadCaseForSession2 basicModel(Integer tableNo) {
            return new BadCaseForSession2(false, null, tableNo, null);
        }

        // evaluator가 BAD 일 경우, 값 업데이트
        private BadCaseForSession2 withBadCase(
                Boolean newIsBadCase,
                Long userId,
                Integer tableNo,
                Integer levelDifference
        ) {
            return new BadCaseForSession2(newIsBadCase, userId, tableNo, levelDifference);
        }

    }

    // Round2 테이블링에서 테이블별 상태를 추적하기 위한 집계(통계) 레코드
    private record RoundTableSchema(
            int tableNo,
            Integer tableSize,
            Integer maxLevel,
            Integer minLevel,
            Integer levelSum // 평균 계산용
    ) {

        // 기본값
        private static RoundTableSchema basicModel(Integer tableNo) {
            return new RoundTableSchema(tableNo, 0, 0, 99, 0);
        }

        // 평균 리턴용
        private Double getAvg() {
            return tableSize == 0 ? 0.0 : levelSum.doubleValue() / tableSize;
        }

    }

    private record DonorTableInfo(
            int tableNo,
            int tableSize,
            int femaleCnt,
            int targetDonorSetter, // 0 : 타겟X, 1 : 남성 도너, 2: 여성 도너
            List<Long> candidateUserIdList
    ) {
    }

    // swap 정보용 record
    private record SwapPair(
            Long femaleUserId,
            int femaleTableNo,
            Long maleUserId,
            int maleTableNo
    ) {
    }

}
