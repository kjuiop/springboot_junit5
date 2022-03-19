package com.gig.springboot_junit5.entity;

import com.gig.springboot_junit5.entity.type.StudyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : JAKE
 * @date : 2022/03/19
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Study {

    private StudyStatus studyStatus = StudyStatus.DRAFT;

    private int limit;

    public Study(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit는 0보다 커야 한다.");
        }
        this.limit = limit;
    }
}
