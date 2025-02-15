package com.example.googletaskproject.domain;

public class TimeZoneModel {
        private final String timeZoneId;
        private final String DisplayName;

        public TimeZoneModel(String timeZoneId, String gmtOffset) {
            this.timeZoneId = timeZoneId;
            this.DisplayName = gmtOffset;
        }

        public String getTimeZoneId() {
            return timeZoneId;
        }

        public String getDisplayName() {
            return DisplayName;
        }
    }