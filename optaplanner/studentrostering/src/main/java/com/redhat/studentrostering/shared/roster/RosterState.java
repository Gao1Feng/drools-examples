/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.studentrostering.shared.roster;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redhat.studentrostering.shared.common.AbstractPersistable;
import com.redhat.studentrostering.shared.common.HasTimeslot;
import com.redhat.studentrostering.shared.shift.Shift;
import com.redhat.studentrostering.shared.tenant.Tenant;

public class RosterState extends AbstractPersistable {

	private static final long serialVersionUID = -4945343006210450428L;
	
	@NotNull
    private Integer publishNotice; // In number of days
    @NotNull
    private LocalDate firstDraftDate;
    @NotNull
    private final Integer publishLength; // In number of days
    @NotNull
    private Integer draftLength; // In number of days
    @NotNull
    private Integer unplannedRotationOffset; // In number of days from reference point
    @NotNull
    @Min(2) // Min 2 since it is impossible to do wrapping shifts templates on single day rotations
    private Integer rotationLength; // In number of days
    @NotNull
    private LocalDate lastHistoricDate;
    @NotNull
    private ZoneId timeZone;

    @NotNull
    private Tenant tenant;

    public RosterState() {
        super(-1);
        publishLength = 7;
    }

    public RosterState(Integer tenantId, Integer publishNotice, LocalDate firstDraftDate, Integer publishLength, Integer draftLength, Integer unplannedRotationOffset, Integer rotationLength, LocalDate lastHistoricDate,
                       ZoneId timeZone) {
        super(tenantId);
        this.publishNotice = publishNotice;
        this.firstDraftDate = firstDraftDate;
        this.publishLength = publishLength;
        this.draftLength = draftLength;
        this.unplannedRotationOffset = unplannedRotationOffset;
        this.rotationLength = rotationLength;
        this.lastHistoricDate = lastHistoricDate;
        this.timeZone = timeZone;
    }

    @JsonIgnore
    public boolean isHistoric(OffsetDateTime dateTime) {
        return dateTime.isBefore(OffsetDateTime.of(getFirstPublishedDate().atTime(LocalTime.MIDNIGHT), dateTime.getOffset()));
    }

    @JsonIgnore
    public boolean isDraft(OffsetDateTime dateTime) {
        return !dateTime.isBefore(OffsetDateTime.of(getFirstDraftDate().atTime(LocalTime.MIDNIGHT), dateTime.getOffset()));
    }

    @JsonIgnore
    public boolean isPublished(OffsetDateTime dateTime) {
        return !isHistoric(dateTime) && !isDraft(dateTime);
    }

    @JsonIgnore
    public boolean isHistoric(LocalDateTime dateTime) {
        return dateTime.isBefore(getFirstPublishedDate().atTime(LocalTime.MIDNIGHT));
    }

    @JsonIgnore
    public boolean isDraft(LocalDateTime dateTime) {
        return !dateTime.isBefore(getFirstDraftDate().atTime(LocalTime.MIDNIGHT));
    }

    @JsonIgnore
    public boolean isPublished(LocalDateTime dateTime) {
        return !isHistoric(dateTime) && !isDraft(dateTime);
    }

    @JsonIgnore
    public boolean isHistoric(Shift shift) {
        return isHistoric(shift.getStartDateTime());
    }

    @JsonIgnore
    public boolean isDraft(Shift shift) {
        return isDraft(shift.getStartDateTime());
    }

    @JsonIgnore
    public boolean isPublished(Shift shift) {
        return isPublished(shift.getStartDateTime());
    }

    @JsonIgnore
    public boolean isHistoric(HasTimeslot shift) {
        return isHistoric(HasTimeslot.EPOCH.plus(shift.getDurationBetweenReferenceAndStart()));
    }

    @JsonIgnore
    public boolean isDraft(HasTimeslot shift) {
        return isDraft(HasTimeslot.EPOCH.plus(shift.getDurationBetweenReferenceAndStart()));
    }

    @JsonIgnore
    public boolean isPublished(HasTimeslot shift) {
        return isPublished(HasTimeslot.EPOCH.plus(shift.getDurationBetweenReferenceAndStart()));
    }

    @JsonIgnore
    public LocalDate getFirstPublishedDate() {
        return lastHistoricDate.plusDays(1);
    }

    @JsonIgnore
    public LocalDate getFirstUnplannedDate() {
        return firstDraftDate.plusDays(draftLength);
    }

    @JsonIgnore
    public LocalDate getPublishDeadline() {
        return firstDraftDate.minusDays(publishNotice);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public Integer getPublishNotice() {
        return publishNotice;
    }

    public void setPublishNotice(Integer publishNotice) {
        this.publishNotice = publishNotice;
    }

    public Integer getDraftLength() {
        return draftLength;
    }

    public void setDraftLength(Integer draftLength) {
        this.draftLength = draftLength;
    }

    @JsonIgnore
    public Integer getPublishLength() {
        return publishLength;
    }

    public Integer getRotationLength() {
        return rotationLength;
    }

    public void setRotationLength(Integer rotationLength) {
        this.rotationLength = rotationLength;
    }

    public LocalDate getFirstDraftDate() {
        return firstDraftDate;
    }

    public void setFirstDraftDate(LocalDate firstDraftDate) {
        this.firstDraftDate = firstDraftDate;
    }

    public Integer getUnplannedRotationOffset() {
        return unplannedRotationOffset;
    }

    public void setUnplannedRotationOffset(Integer unplannedOffset) {
        this.unplannedRotationOffset = unplannedOffset;
    }

    public void setLastHistoricDate(LocalDate lastHistoricDate) {
        this.lastHistoricDate = lastHistoricDate;
    }

    public LocalDate getLastHistoricDate() {
        return lastHistoricDate;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
