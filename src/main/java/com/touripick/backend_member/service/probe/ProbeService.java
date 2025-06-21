package com.touripick.backend_member.service.probe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProbeService {
    // 데이터베이스와 같은 외부 의존성을 예시로 주입합니다.

    /**
     * -- GETTER --
     *  현재 초기화 상태를 반환합니다.
     *
     * @return 초기화 완료 시 true
     */
    // 애플리케이션의 내부 상태를 관리하는 플래그 (예: 초기화 완료 여부)

    /**
     * Liveness Probe: 애플리케이션이 회복 불가능한 상태인지 확인합니다.
     * 실패 시 컨테이너가 재시작됩니다.
     * 예: 주요 스레드 데드락, 메모리 부족으로 인한 영구적 장애 상태 등
     */
    public void validateLiveness() {
        // TODO: 이곳에 애플리케이션을 반드시 재시작해야만 하는 치명적인 상태를 확인하는 로직을 구현합니다.
        // 예를 들어, 필수적인 백그라운드 스레드가 죽었거나, 회복 불가능한 설정 오류가 발생한 경우입니다.
        // 현재는 특별한 로직이 없으므로 항상 성공으로 간주합니다.
        //
        // if (isUnrecoverableState()) {
        //     log.error("Liveness probe failed: Unrecoverable state detected.");
        //     throw new RuntimeException("Liveness probe failed due to an unrecoverable state.");
        //
      }

    /**
     * Readiness Probe: 애플리케이션이 현재 요청을 처리할 수 있는 상태인지 확인합니다.
     * 실패 시 서비스에서 제외되어 트래픽을 받지 않습니다.
     * 예: 외부 시스템(DB, API) 연결 문제, 초기화 진행 중, 과부하 상태 등
     */
    public void validateReadiness() {
            // TODO: 만일 서비스 수행일 일시 중지해야 하는 상황이면 exception발생
        // 1. 외부 시스템 (예: 데이터베이스) 연결 상태 확인
        // 2. 애플리케이션 내부 초기화 작업 완료 여부 확인
        // TODO: 이 외에도 캐시 워밍업, 주요 설정 로딩 완료 등
        //       서비스가 요청을 처리하기 위해 반드시 필요한 조건들을 추가로 확인합니다.
      }

    /**
     * 애플리케이션 초기화 완료 상태를 변경하는 메소드 예시
     * @param initialized 초기화 완료 여부
     */
}