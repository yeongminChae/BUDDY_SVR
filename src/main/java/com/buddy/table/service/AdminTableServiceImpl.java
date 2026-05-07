package com.buddy.table.service;

import com.buddy.table.dto.GetTableAssignmentsResponse;
import com.buddy.table.dto.TableAssignmentGroupResponse;
import com.buddy.table.dto.TableAssignmentRow;
import com.buddy.table.dto.TableMemberResponse;
import com.buddy.table.dto.update.TableMoveChangeRequest;
import com.buddy.table.dto.update.UpdateTableAssignmentsRequest;
import com.buddy.table.dto.update.UpdateTableAssignmentsResponse;
import com.buddy.table.model.RoundAssignmentRow;
import com.buddy.table.model.TableAssignment;
import com.buddy.table.model.TableAssignmentUpdateCommand;
import com.buddy.table.repository.TableRepository;
import com.buddy.table.service.helper.TableingRoundHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTableServiceImpl implements AdminTableService {

    private final TableRepository tableRepository;
    private final TableingRoundHelper helper;

    @Override
    public void runTableing(Long sessionId) {
        // 0) 확정자 로드
        List<RoundAssignmentRow> participants =
                tableRepository.findConfirmedParticipants(sessionId);

        if (participants.isEmpty()) {
            throw new IllegalArgumentException("참여자가 없습니다. 다시 한 번 확인하세요");
        }

        // 0.1 ) 세션 호스트 아이디
        Long hostUserId = tableRepository.getHostUserId(sessionId);

        // 1.1 세션1 테이블 정원 결정 (MVP: 일단 전부 4인으로)
        List<TableAssignment> table1Assignments = helper.runTableingRound1(sessionId, hostUserId, participants);

        // 1.2 저장 이전 해당 세션 더미 및 찌거기 데이터 제거
        tableRepository.deleteAssignmentsBySession(sessionId);

        // 1.3 저장
        int table1Result = tableRepository.insertRoundAssignments(sessionId, table1Assignments);
        if (table1Result != participants.size()) {
            throw new IllegalArgumentException("저장 결과 확인 필요");
        }

        // 2.1 세션2 테이블 정원 결정 (MVP: 일단 전부 4인으로)
        List<TableAssignment> table2Assignments = helper.runTableingRound2(sessionId, hostUserId, participants, table1Assignments);

        // 2.2 저장
        int table2Result = tableRepository.insertRoundAssignments(sessionId, table2Assignments);
        if (table2Result != participants.size()) {
            throw new IllegalArgumentException("저장 결과 확인 필요");
        }

    }

    @Override
    public GetTableAssignmentsResponse findTableAssignmentRows(Long sessionId) {
        List<TableAssignmentRow> rows =
                tableRepository.findTableAssignmentRows(sessionId);

        if (rows == null) throw new IllegalArgumentException("테이블 결과 조회를 실패 했습니다.");

        List<TableAssignmentGroupResponse> toTableGroups1 =
                toTableGroups(
                        // 라운드는 rows를 필터해서 1인 값만 보내서 결과 리스트 만들어옴
                        rows.stream()
                                .filter(i -> i.roundNo() == 1)
                                .toList()
                );

        List<TableAssignmentGroupResponse> toTableGroups2 =
                toTableGroups(
                        // 라운드는 rows를 필터해서 2인 값만 보내서 결과 리스트 만들어옴
                        rows.stream()
                                .filter(i -> i.roundNo() == 2)
                                .toList()
                );

        return new GetTableAssignmentsResponse(
                sessionId,
                toTableGroups1,
                toTableGroups2
        );
    }

    private List<TableAssignmentGroupResponse> toTableGroups(List<TableAssignmentRow> rows) {
        // grouped 의 각 entry 는
        // key   = tableNo
        // value = 그 테이블에 속한 사람들(row 리스트)
        Map<Integer, List<TableAssignmentRow>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        // 어떤 기준으로 묶을지 결정
                        // 여기서는 row.tableNo() 값을 기준으로 묶는다.
                        TableAssignmentRow::tableNo,
                        // 결과 Map 타입
                        // LinkedHashMap 을 써서 입력 순서를 유지한다.
                        LinkedHashMap::new,
                        // 같은 tableNo 로 묶인 row 들을 List 로 모은다.
                        // 결과적으로 grouped 는 아래 같은 구조가 된다.
                        Collectors.toList()
                ));

        return grouped.entrySet().stream()
                .map(entry -> new TableAssignmentGroupResponse(
                        // entry.getKey() 는 테이블 번호
                        entry.getKey(),
                        // entry.getValue() 는 해당 테이블에 속한 사람들 리스트
                        // 각 row 를 TableMemberResponse 로 변환한다.
                        entry.getValue().stream()
                                .map(row -> new TableMemberResponse(
                                        row.userId(),
                                        row.name(),
                                        row.nickname(),
                                        row.email(),
                                        row.level()
                                ))
                                .toList()
                ))
                .toList();

    }

    @Override
    public UpdateTableAssignmentsResponse updateTableAssignments(Long sessionId, UpdateTableAssignmentsRequest request) {
        List<TableAssignmentUpdateCommand> changes = new ArrayList<>();

        for (TableMoveChangeRequest req : request.changes()) {
            TableAssignmentUpdateCommand command = new TableAssignmentUpdateCommand(
                    sessionId,
                    req.userId(),
                    req.roundNo(),
                    req.toTableNo()
            );

            changes.add(command);
        }

        int result = tableRepository.updateTableAssignments(sessionId, changes);
        if (result == 0) throw new IllegalArgumentException("업데이트 중 에러 발생");

        return new UpdateTableAssignmentsResponse(sessionId, result);
    }

}
