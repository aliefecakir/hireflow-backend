package com.hireflow.backend.util;

import com.hireflow.backend.entity.Form;

import java.time.LocalDateTime;

public final class FormWindow {

    public static final Short ACTIVE = 1;
    public static final Short PASSIVE = 0;

    private FormWindow() {
    }

    public static boolean isActive(Form form) {
        return form != null && ACTIVE.equals(form.getIsActv());
    }

    public static boolean isExpired(Form form, LocalDateTime now) {
        return form != null && form.getEdate() != null && now.isAfter(form.getEdate());
    }

    public static boolean hasStarted(Form form, LocalDateTime now) {
        return form != null && (form.getSdate() == null || !now.isBefore(form.getSdate()));
    }

    public static boolean isVisibleToCandidates(Form form, LocalDateTime now) {
        return isActive(form) && !isExpired(form, now);
    }

    public static boolean canApply(Form form, LocalDateTime now) {
        return isVisibleToCandidates(form, now) && hasStarted(form, now);
    }

    public static Short resolveActiveFlag(Short requested, LocalDateTime edate, LocalDateTime now) {
        Short flag = requested != null ? requested : ACTIVE;
        if (edate != null && now.isAfter(edate)) {
            return PASSIVE;
        }
        return flag;
    }
}
