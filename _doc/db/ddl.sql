CREATE TABLE `HOST` (
  `IDX` bigint NOT NULL AUTO_INCREMENT COMMENT '고유 PK',
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '호스트 이름',
  `HOST` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '연결 호스트 주소',
  `TYPE` enum('MQTT','TCP','WS','HTTP') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '연결타입',
  `TOPIC` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '연결 호스트 토픽',
  `HOST_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '연결 타입의 아이디',
  `PASSWORD` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '연결 타입의 암호',
  `CREATE_DT` datetime NOT NULL COMMENT '생성일시',
  `CREATE_AT` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '생성자',
  `UPDATE_DT` datetime NOT NULL COMMENT '수정일시',
  `UPDATE_AT` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '수정자',
  PRIMARY KEY (`IDX`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='연결 호스트 정보';

CREATE TABLE `FORMAT` (
  `IDX` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK 고유값',
  `NAME` varchar(100) NOT NULL COMMENT '포맷 명',
  `FORMAT` longtext NOT NULL COMMENT '포맷팅',
  `CONTENT_TYPE` varchar(50) DEFAULT NULL COMMENT '데이터 포맷 컨텐트 타입',
  `CREATE_DT` datetime NOT NULL COMMENT '생성일시',
  `UPDATE_DT` datetime NOT NULL COMMENT '수정일시',
  `CREATE_AT` varchar(100) NOT NULL COMMENT '생성자',
  `UPDATE_AT` varchar(100) NOT NULL COMMENT '수정자',
  PRIMARY KEY (`IDX`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='데이터 포멧 테이블';