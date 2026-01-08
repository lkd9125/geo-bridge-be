package com.geo.bridge.global.base;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * 페이징 RS
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #totalPage} : 전체 페이지 수</li>
 *     <li>{@link #totalCount} : 전체 리스트 수</li>
 *     <li>{@link #page} : 현재 페이지</li>
 *     <li>{@link #size} : 사이즈</li>
 *     <li>{@link #range} : 범위</li>
 *     <li>{@link #list} : 목록 리스트(데이터)</li>
 *     <li>{@link #stPage} : 시작 페이지</li>
 *     <li>{@link #endPage} : 끝 페이지</li>
 *     <li>{@link #prevPage} : 이전 페이지</li>
 *     <li>{@link #nextPage} : 다음 페이지</li>
 * </ul>
 * 
 * <p>메소드</p>
 * <ul>
 *     <li>{@link #of(List, int, int ,int ,int)} : 메타데이터 구하는 유틸, 기본적인 데이터 Setting도 가능</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasePageRS<T> {

    private Integer totalPage; // 전체 페이지 수

    private Integer totalCount; // 전체 리스트 수

    private Integer page; // 현재 페이지
    private Integer size;
    private Integer range;


    private List<T> list; // 목록 리스트

    private Integer stPage; // 시작 페이지

    private Integer endPage; // 끝 페이지

    private Integer prevPage; // 이전 페이지

    private Integer nextPage; // 다음 페이지

    public static <T> BasePageRS<T> of(
            List<T> data,
            int page,
            int size,
            int range,
            int totalCount
    ) {
        BasePageRS<T> basePageRs = new BasePageRS<>();
        basePageRs.list = data;
        basePageRs.page = page;
        basePageRs.size = size;
        basePageRs.range = range;
        basePageRs.totalCount = totalCount;
        if(totalCount != 0) {
            basePageRs.totalPage = (int) Math.ceil((double) totalCount / size);
            basePageRs.stPage = (int) (Math.floor((double) (page - 1) / range) * range) + 1;
            basePageRs.endPage = Math.min(basePageRs.stPage + range - 1, basePageRs.totalPage);
            basePageRs.prevPage = page > 1 ? page - 1 : null;
            basePageRs.nextPage = page < basePageRs.totalPage ? page + 1 : null;
        } else {
            basePageRs.totalPage = 0;
        }
        return basePageRs;
    }

}
